package kr.co.g2e.appname;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import kr.co.g2e.utils.cache.Cache;

@SpringBootApplication
@MapperScan(basePackages = "kr.co.g2e.appname", annotationClass = Mapper.class)
public class Application extends SpringBootServletInitializer {
	/**
	 * 스프링부트 메인
	 */
	public static void main(String[] args) {
		Cache.init(); // 캐시 초기화
		SpringApplication.run(Application.class, args);
	}

	/**
	 * war로 패키징 하기 위해 필요한 구성(서블릿 초기화)
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		Cache.init(); // 캐시 초기화
		return application.sources(Application.class);
	}
}