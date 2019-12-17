package press.whcj.ams.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import press.whcj.ams.support.BaseController;

/**
 * log aspect
 *
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Component
@Aspect
public class LogAspect {

	@Around("@annotation(org.springframework.web.bind.annotation.PostMapping) || @annotation(org.springframework.web.bind.annotation.RequestMapping)")
	public Object process(ProceedingJoinPoint joinPoint) throws Throwable {
		Object target = joinPoint.getTarget();
		if (target instanceof BaseController) {
			BaseController controller = (BaseController) target;
			String methodName = joinPoint.getSignature().getName();
			controller.logger.info(methodName + " start------");
			Object result = joinPoint.proceed();
			controller.logger.info(methodName + " end------");
			return result;
		}
		return joinPoint.proceed();
	}
}
