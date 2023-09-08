package whu.edu.cn.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import springfox.documentation.annotations.ApiIgnore;
import whu.edu.cn.util.LivyUtil;

import javax.annotation.Resource;

@ApiIgnore
@Controller
public class LivyController {
    @Resource
    private LivyUtil livyUtil;

    @PostMapping(value = "/initLivy")
    public void initLivy() {
        livyUtil.initLivy();
    }
}
