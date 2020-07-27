package press.whcj.ams.service.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.querydsl.QSort;
import org.springframework.session.data.mongo.MongoIndexedSessionRepository;
import org.springframework.session.data.mongo.MongoSession;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import press.whcj.ams.common.ColumnName;
import press.whcj.ams.common.Constant;
import press.whcj.ams.entity.MongoPage;
import press.whcj.ams.entity.User;
import press.whcj.ams.entity.dto.UserDTO;
import press.whcj.ams.entity.vo.UserVO;
import press.whcj.ams.exception.ResultCode;
import press.whcj.ams.exception.ServiceException;
import press.whcj.ams.service.UserService;
import press.whcj.ams.util.FastUtils;
import press.whcj.ams.util.JsonUtils;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Service
public class UserServiceImpl implements UserService {
    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private MongoIndexedSessionRepository sessionRepository;

    @Override
    public String save(UserDTO userDTO) {
        boolean isUpdate = userDTO.getId() != null;
        User user;
        if (isUpdate) {
            user = mongoTemplate.findById(userDTO.getId(), User.class);
            FastUtils.checkNull(user);
            Objects.requireNonNull(user).setUpdate(null);
            user.setUpdateTime(null);
        } else {
            user = new User();
        }
        if (userDTO.getPassword() != null) {
            if (!StringUtils.isEmpty(userDTO.getOldPwd())
                    && !digestPassword(userDTO.getOldPwd()).equals(user.getPassword())) {
                throw new ServiceException(ResultCode.OLD_PWD_ERROR);
            }
            userDTO.setPassword(digestPassword(userDTO.getPassword()));
        }
        if (userDTO.getLoginName() != null) {
            synchronized (userDTO.getLoginName().intern()) {
                Criteria checkLoginNameCriteria = Criteria.where(ColumnName.LOGIN_NAME).is(userDTO.getLoginName())
                        .and(ColumnName.IS_DEL).ne(Constant.Is.YES);
                if (isUpdate) {
                    checkLoginNameCriteria = checkLoginNameCriteria.and(ColumnName.ID).ne(userDTO.getId());
                }
                User checkLoginNameExist = mongoTemplate.findOne(new Query(checkLoginNameCriteria), User.class);
                if (checkLoginNameExist != null) {
                    throw new ServiceException(ResultCode.LOGIN_NAME_EXIST);
                }
                mongoTemplate.save(FastUtils.copyProperties(userDTO, user));
            }
        } else {
            mongoTemplate.save(FastUtils.copyProperties(userDTO, user));
        }
        return user.getId();
    }

    @Override
    public void remove(UserDTO userDTO, UserVO operator) {
        List<String> ids = userDTO.getIds();
        FastUtils.checkParams(ids);
        mongoTemplate.updateMulti(new Query(Criteria.where(ColumnName.ID).in(ids)),
                Update.update(ColumnName.IS_DEL, Constant.Is.YES)
                        .set(ColumnName.UPDATE_TIME, LocalDateTime.now())
                        .set(ColumnName.UPDATE, new User(operator.getId())),
                User.class);
    }

    @Override
    public UserVO findDetail(String id) {
        UserVO userVo = mongoTemplate.findById(id, UserVO.class, Constant.CollectionName.USER);
        FastUtils.checkNull(userVo);
        Objects.requireNonNull(userVo).setPassword(null);
        return userVo;
    }

    @Override
    public MongoPage<UserVO> findPage(UserDTO userDTO) {
        MongoPage<UserVO> page = userDTO.getPage();
        Query query = new Query(Criteria.where(ColumnName.IS_ADMIN).ne(Constant.Is.YES)
                .and(ColumnName.IS_DEL).ne(Constant.Is.YES));
        query.with(page.buildPageRequest()).with(QSort.by(Sort.Direction.DESC, ColumnName.UPDATE_TIME));
        long total = mongoTemplate.count(query, User.class);
        page.setTotal(total);
        if (total == 0L) {
            return page;
        }
        return page.setRecords(mongoTemplate.find(query, UserVO.class, Constant.CollectionName.USER));
    }

    @Override
    public void init() {
        User existAdmin = mongoTemplate.findOne(new Query(Criteria.where(ColumnName.LOGIN_NAME).is(Constant.SysConfig.ADMIN_LOGIN_NAME)), User.class);
        if (existAdmin == null) {
            User admin = new User();
            admin.setUserName(Constant.SysConfig.ADMIN_LOGIN_NAME);
            admin.setLoginName(Constant.SysConfig.ADMIN_LOGIN_NAME);
            admin.setPassword(digestPassword(Constant.SysConfig.ADMIN_PWD));
            admin.setIsAdmin(Constant.Is.YES);
            admin.setCreateTime(LocalDateTime.now());
            admin.setUpdateTime(LocalDateTime.now());
            mongoTemplate.insert(admin);
        }
    }

    private String digestPassword(String pwd) {
        return DigestUtils.md5DigestAsHex((pwd + Constant.SysConfig.SALT).getBytes());
    }

    @Override
    public UserVO login(UserDTO userDTO, HttpServletRequest request) {
        FastUtils.checkParams(userDTO.getLoginName(), userDTO.getPassword());
        UserVO existUser = mongoTemplate.findOne(new Query(
                        Criteria.where(ColumnName.LOGIN_NAME).is(userDTO.getLoginName())
                                .and(ColumnName.IS_DEL).ne(Constant.Is.YES)),
                UserVO.class, Constant.CollectionName.USER);
        if (existUser == null) {
            throw new ServiceException(ResultCode.LOGIN_NAME_OR_PASSWORD_ERROR);
        }
        if (!digestPassword(userDTO.getPassword()).equals(existUser.getPassword())) {
            throw new ServiceException(ResultCode.LOGIN_NAME_OR_PASSWORD_ERROR);
        }
        existUser.setPassword(null);
        MongoSession session = sessionRepository.createSession();
        session.setAttribute(Constant.SessionKey.USER_INFO, JsonUtils.object2Json(existUser));
        session.setLastAccessedTime(Instant.now());
        sessionRepository.save(session);
        return existUser.setAuth(session.getId());
    }
}
