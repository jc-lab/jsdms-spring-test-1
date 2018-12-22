package kr.jclab.jsdms.spring.test1;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TestRevisionedWebController {
    @RequestMapping(path = "/app/{revision}/installer")
    public ModelAndView installer() {
        ModelAndView modelAndView = new ModelAndView("view/installer");
        return modelAndView;
    }
}
