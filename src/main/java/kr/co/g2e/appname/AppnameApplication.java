package kr.co.g2e.appname;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import kr.co.g2e.utils.filter.RequestLoggingFilter;
import kr.co.g2e.utils.util.Params;

@SpringBootApplication
@MapperScan("kr.co.g2e.appname")
public class AppnameApplication extends SpringBootServletInitializer implements WebMvcConfigurer {
	public static void main(String[] args) {
		SpringApplication.run(AppnameApplication.class, args);
	}

	/**
	 * war로 패키징 하기 위해 필요한 구성(서블릿 초기화)
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(AppnameApplication.class);
	}

	/**
	 * 요청 로깅 필터 등록(헤더, 쿠키, 파라미터 정보, 실행시간 정보 조회)
	 */
	@Bean
	public FilterRegistrationBean<RequestLoggingFilter> requestLoggingFilter() {
		FilterRegistrationBean<RequestLoggingFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new RequestLoggingFilter());
		registrationBean.addUrlPatterns("*.do");
		return registrationBean;
	}

	/**
	 * 인터셉터 등록
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new LoginCheckInterceptor()).addPathPatterns("/**/*.do"); // 로그인체크
	}

	/**
	 * Argument 리졸버 등록
	 */
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(new ParamsResolver()); // Params
	}

	/**
	 * 로그인 체크 인터셉터 정의
	 */
	public static class LoginCheckInterceptor implements HandlerInterceptor {
		private Logger logger = LoggerFactory.getLogger(getClass());

		@Override
		public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
			logger.debug("===================== 로그인 체크 성공");
			return true; // true 이면 계속 진행, false 이면 다음 호출 중단
		}
	}

	/**
	 * Params Argument 리졸버 정의
	 */
	public static class ParamsResolver implements HandlerMethodArgumentResolver {
		@Override
		public boolean supportsParameter(MethodParameter parameter) {
			return Params.class.isAssignableFrom(parameter.getParameterType());
		}

		@Override
		public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
			return Params.getParams((HttpServletRequest) webRequest.getNativeRequest()); // 요청객체를 파싱하여 파라미터를 얻어온다.
		}
	}
}