package press.whcj.ams.web;

import java.io.File;
import java.io.FileInputStream;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import press.whcj.ams.config.MongoPoolProperties;
import press.whcj.ams.exception.ResultCode;
import press.whcj.ams.exception.ServiceException;
import press.whcj.ams.support.BaseController;
import press.whcj.ams.support.Result;

/**
 * 备份还原
 *
 * @author xyyxhcj@qq.com
 * @since 2021/01/22
 */
@RestController
@RequestMapping("backup")
public class BackupController extends BaseController {
    /**
     * 导出命令host,dbName,outDir,username,password
     **/
    private final static String DUMP_COMMAND = "mongodump -h %s -d %s -o %s  -u %s -p %s";
    /**
     * 临时文件路径
     **/
    private final static String OUT_PATH = "/usr/temp/";
    /**
     * save to .tar.gz
     * targetFile,sourceFile
     */
    private final static String TAR_COMMAND = "tar -zcvPf %s %s";

    @Resource
    private MongoPoolProperties mongoProperties;

    @PostMapping("export")
    public void export(HttpServletResponse response) throws Exception {
        String encoding = "gbk";
        FileUtils.forceMkdir(new File(OUT_PATH));
        long now = System.currentTimeMillis();
        String sourceFilePath = OUT_PATH + now;
        String command = String.format(DUMP_COMMAND, mongoProperties.getAddress()[0], mongoProperties.getDatabase(), sourceFilePath, mongoProperties.getUsername(), new String(mongoProperties.getPassword()));
        logger.info(command);
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(command);
        logger.debug(IOUtils.toString(process.getErrorStream(), encoding));
        int exit = process.waitFor();
        if (exit != 0) {
            throw new ServiceException(ResultCode.OPERATION_FAILURE);
        }
        String targetFilePath = sourceFilePath + ".tar.gz";
        command = String.format(TAR_COMMAND, targetFilePath, sourceFilePath);
        logger.info(command);
        process = runtime.exec(command);
        exit = process.waitFor();
        if (exit != 0) {
            throw new ServiceException(ResultCode.OPERATION_FAILURE);
        }
        logger.debug(IOUtils.toString(process.getErrorStream(), encoding));
        response.setContentType("application/x-compressed-tar");
        File targetFile = new File(targetFilePath);
        IOUtils.copyLarge(new FileInputStream(targetFile), response.getOutputStream());
        FileUtils.forceDelete(new File(sourceFilePath));
        FileUtils.forceDelete(targetFile);
    }

    @PostMapping("load")
    public Result<Object> load() throws Exception {
        // cjTodo 2021-01-22 导入数据，需要提供数据库名
        return ok();
    }
}
