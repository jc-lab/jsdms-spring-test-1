package kr.jclab.jsdms.spring.test1;

import kr.jclab.jsdms.spring.client.annotations.EnableJsDMSSpringClient;
import kr.jclab.jsdms.spring.client.service.JsDMSSpringClientService;
import kr.jclab.jsdms.spring.revisionedweb.FindRepositoryDirectoryHandler;
import kr.jclab.jsdms.spring.revisionedweb.JsDMSSpringRevisionedWebConfigurerAdapter;
import kr.jclab.jsdms.spring.revisionedweb.RevisionedWebBuilder;
import kr.jclab.jsdms.spring.revisionedweb.RevisionedWebResolver;
import kr.jclab.jsdms.spring.revisionedweb.annotations.EnableJsDMSSpringRevisionedWeb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;

import javax.servlet.ServletContext;
import java.io.File;

@Configuration
@EnableWebMvc
@EnableJsDMSSpringClient
@EnableJsDMSSpringRevisionedWeb
public class WebConfig implements WebMvcConfigurer, JsDMSSpringRevisionedWebConfigurerAdapter {
    @Autowired
    private RevisionedWebResolver revisionedWebResolver;
    @Autowired
    private JsDMSSpringClientService dmsSpringClientService;

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/myweb/**")
                .resourceChain(true)
                .addResolver(revisionedWebResolver.resourceResolver());
    }

    @Bean
    public MessageSource messageSource() {
        return revisionedWebResolver.messageSource();
    }

    @Bean
    public SpringTemplateEngine templateEngine(ServletContext servletContext) {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        AbstractConfigurableTemplateResolver templateResolver = revisionedWebResolver.templateResolver();
        templateResolver.setPrefix("/templates/");
        templateResolver.setSuffix(".html");
        engine.setTemplateResolver(templateResolver);
        engine.setLinkBuilder(revisionedWebResolver.linkBuilder());
        engine.setMessageSource(messageSource());
        return engine;
    }

    @Override
    public void configure(RevisionedWebBuilder builder) {
        builder
                .addRepository("myweb", "/myweb/{revision}/**", dmsSpringClientService.findRepoDirByName("myweb"))
                    .messageSourceBasename("i18n/messages")
                    .and()
                // dmsSpringClientService.findRepoDirByName("myweb") 대신 null을 쓰고 아래와 같이 handler를 이용할수도 있음
                .setFindRepositoryDirectoryHandler(new FindRepositoryDirectoryHandler() {
                    @Override
                    public File findByName(String repoName) {
                        return dmsSpringClientService.findRepoDirByName(repoName);
                    }
                });
    }
}
