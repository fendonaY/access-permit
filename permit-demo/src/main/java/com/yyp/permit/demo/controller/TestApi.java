package com.yyp.permit.demo.controller;

import com.yyp.permit.demo.model.dto.BuyGoodsDto;
import com.yyp.permit.demo.service.StockService;
import com.yyp.ulog.core.LogGlobalConfig;
import com.yyp.ulog.core.ULogManager;
import com.yyp.ulog.interceptor.LogInterceptor;
import com.yyp.ulog.interceptor.MDCInterceptor;
import com.yyp.ulog.weaver.ULog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@RestController
@RequestMapping("/permit/test")
@Slf4j
public class TestApi {

    @Resource
    private StockService stockService;
    @Resource
    private ULogManager uLogManager;

    @Resource
    private LogGlobalConfig logGlobalConfig ;

    @PostConstruct
    public void init(){
        logGlobalConfig.setGlobalPrint(true);
    }



    @PostMapping("/buy")
    @ULog(type = "123", module = "123", desc = "测试日志：{}", ignoreResult = false)
    public String buy(@RequestBody BuyGoodsDto buyGoodsDto) {
        stockService.buy(buyGoodsDto);
        uLogManager.getContext().getULogHolder().formatDesc("asdfasdf");
        StringBuffer string = new StringBuffer("\n" +
                "\n" +
                "<!DOCTYPE html>\n" +
                "<html lang=\"zh-CN\">\n" +
                "<head>\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <link rel=\"canonical\" href=\"https://blog.csdn.net/qq_42008471/article/details/117842065\"/>\n" +
                "    <meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">\n" +
                "    <meta name=\"renderer\" content=\"webkit\"/>\n" +
                "    <meta name=\"force-rendering\" content=\"webkit\"/>\n" +
                "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge,chrome=1\"/>\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no\">\n" +
                "    <meta name=\"report\" content='{\"pid\": \"blog\", \"spm\":\"1001.2101\"}'>\n" +
                "    <meta name=\"referrer\" content=\"always\">\n" +
                "    <meta http-equiv=\"Cache-Control\" content=\"no-siteapp\" /><link rel=\"alternate\" media=\"handheld\" href=\"#\" />\n" +
                "    <meta name=\"shenma-site-verification\" content=\"5a59773ab8077d4a62bf469ab966a63b_1497598848\">\n" +
                "    <meta name=\"applicable-device\" content=\"pc\">\n" +
                "    <link  href=\"https://g.csdnimg.cn/static/logo/favicon32.ico\"  rel=\"shortcut icon\" type=\"image/x-icon\" />\n" +
                "    <title>SpringBoot的拦截器_qq_42008471的博客-CSDN博客_springboot拦截器</title>\n" +
                "    <script>\n" +
                "      (function(){ \n" +
                "        var el = document.createElement(\"script\"); \n" +
                "        el.src = \"https://s3a.pstatp.com/toutiao/push.js?1abfa13dfe74d72d41d83c86d240de427e7cac50c51ead53b2e79d40c7952a23ed7716d05b4a0f683a653eab3e214672511de2457e74e99286eb2c33f4428830\"; \n" +
                "        el.id = \"ttzz\"; \n" +
                "        var s = document.getElementsByTagName(\"script\")[0]; \n" +
                "        s.parentNode.insertBefore(el, s);\n" +
                "      })(window)\n" +
                "    </script>\n" +
                "        <meta name=\"keywords\" content=\"springboot拦截器\">\n" +
                "        <meta name=\"csdn-baidu-search\"  content='{\"autorun\":true,\"install\":true,\"keyword\":\"springboot拦截器\"}'>\n" +
                "    <meta name=\"description\" content=\"该文章来源于互联网：原文档链接1. 拦截器介绍拦截器是在servlet执行之前执行的程序（这里就是controller代码执行之前），它主要是用于拦截用户请求并作相应的处理，比如说可以判断用户是否登录，做相关的日志记录，也可以做权限管理。SpringBoot中的拦截器实现和spring mvc 中是一样的，它的大致流程是，先自己定义一个拦截器类，并将这个类实现一个HandlerInterceptor类，或者是继承HandlerInterceptorAdapter，都可以实现拦截器的定义。然后将自己定义\">\n" +
                "    <script src=\"//g.csdnimg.cn/tingyun/1.8.5/blog.js\" type='text/javascript'></script>\n" +
                "        <link rel=\"stylesheet\" type=\"text/css\" href=\"https://csdnimg.cn/release/blogv2/dist/pc/css/detail_enter-f82be4a60e.min.css\">\n" +
                "    <script type=\"application/ld+json\">{\"@context\":\"https://ziyuan.baidu.com/contexts/cambrian.jsonld\",\"@id\":\"https://blog.csdn.net/qq_42008471/article/details/117842065\",\"appid\":\"1638831770136827\",\"pubDate\":\"2021-06-12T11:19:31\",\"title\":\"SpringBoot的拦截器_qq_42008471的博客-CSDN博客_springboot拦截器\",\"upDate\":\"2021-06-12T11:27:17\"}</script>\n" +
                "        <link rel=\"stylesheet\" type=\"text/css\" href=\"https://csdnimg.cn/release/blogv2/dist/pc/themesSkin/skin-number/skin-number-2c93789924.min.css\">\n" +
                "    <script src=\"https://csdnimg.cn/public/common/libs/jquery/jquery-1.9.1.min.js\" type=\"text/javascript\"></script>\n" +
                "    <script type=\"text/javascript\">\n" +
                "        var isCorporate = false;//注释删除enterprise\n" +
                "        var username =  \"qq_42008471\";\n" +
                "        var skinImg = \"white\";\n" +
                "        var blog_address = \"https://blog.csdn.net/qq_42008471\";\n" +
                "        var currentUserName = \"weixin_46244844\";\n" +
                "        var isOwner = false;\n" +
                "        var loginUrl = \"http://passport.csdn.net/account/login?from=https://blog.csdn.net/qq_42008471/article/details/117842065\";\n" +
                "        var blogUrl = \"https://blog.csdn.net/\";\n" +
                "        var avatar = \"https://profile.csdnimg.cn/F/2/C/3_qq_42008471\";\n" +
                "        var articleTitle = \"SpringBoot的拦截器\";\n" +
                "        var articleDesc = \"该文章来源于互联网：原文档链接1. 拦截器介绍拦截器是在servlet执行之前执行的程序（这里就是controller代码执行之前），它主要是用于拦截用户请求并作相应的处理，比如说可以判断用户是否登录，做相关的日志记录，也可以做权限管理。SpringBoot中的拦截器实现和spring mvc 中是一样的，它的大致流程是，先自己定义一个拦截器类，并将这个类实现一个HandlerInterceptor类，或者是继承HandlerInterceptorAdapter，都可以实现拦截器的定义。然后将自己定义\";\n" +
                "        var articleTitles = \"SpringBoot的拦截器_qq_42008471的博客-CSDN博客_springboot拦截器\";\n" +
                "        var nickName = \"qq_42008471\";\n" +
                "        var articleDetailUrl = \"https://blog.csdn.net/qq_42008471/article/details/117842065\";\n" +
                "        if(window.location.host.split('.').length == 3) {\n" +
                "            blog_address = blogUrl + username;\n" +
                "        }\n" +
                "        var skinStatus = \"White\";\n" +
                "        var blogStaticHost = \"https://csdnimg.cn/release/blogv2/\"\n" +
                "        var isShowConcision = false;\n" +
                "        var isCookieConcision = false\n" +
                "        var isHasDirectoryModel = false\n" +
                "        var isShowSideModel = false\n" +
                "        var isShowDirectoryModel = true\n" +
                "        function getCookieConcision(sName){\n" +
                "            var allCookie = document.cookie.split(\"; \");\n" +
                "            for (var i=0; i < allCookie.length; i++){\n" +
                "                var aCrumb = allCookie[i].split(\"=\");\n" +
                "                if (sName == aCrumb[0])\n" +
                "                    return aCrumb[1];\n" +
                "            }\n" +
                "            return null;\n" +
                "        }\n" +
                "        if (getCookieConcision('blog_details_concision') && getCookieConcision('blog_details_concision') == 0){\n" +
                "            isCookieConcision = true\n" +
                "            isShowSideModel = true\n" +
                "            isShowDirectoryModel = false\n" +
                "        }\n" +
                "    </script>\n" +
                "    <script src=\"https://g.csdnimg.cn/??fixed-sidebar/1.1.6/fixed-sidebar.js\" type=\"text/javascript\"></script>\n" +
                "    <script src='//g.csdnimg.cn/common/csdn-report/report.js' type='text/javascript'></script>\n" +
                "    <link rel=\"stylesheet\" type=\"text/css\" href=\"https://csdnimg.cn/public/sandalstrap/1.4/css/sandalstrap.min.css\">\n" +
                "    <style>\n" +
                "        .MathJax, .MathJax_Message, .MathJax_Preview{\n" +
                "            display: none\n" +
                "        }\n" +
                "    </style>\n" +
                "    <script src=\"https://dup.baidustatic.com/js/ds.js\"></script>\n" +
                "</head>\n" +
                "  <body class=\"nodata \" style=\"\">\n" +
                "        <script>\n" +
                "            var toolbarSearchExt = '{\"landingWord\":[\"springboot拦截器\"],\"queryWord\":\"\",\"tag\":[\"spring boot\"],\"title\":\"SpringBoot的拦截器\"}';\n" +
                "        </script>\n" +
                "    <script src=\"https://g.csdnimg.cn/common/csdn-toolbar/csdn-toolbar.js\" type=\"text/javascript\"></script>\n" +
                "    <script>\n" +
                "    (function(){\n" +
                "        var bp = document.createElement('script');\n" +
                "        var curProtocol = window.location.protocol.split(':')[0];\n" +
                "        if (curProtocol === 'https') {\n" +
                "            bp.src = 'https://zz.bdstatic.com/linksubmit/push.js';\n" +
                "        }\n" +
                "        else {\n" +
                "            bp.src = 'http://push.zhanzhang.baidu.com/push.js';\n" +
                "        }\n" +
                "        var s = document.getElementsByTagName(\"script\")[0];\n" +
                "        s.parentNode.insertBefore(bp, s);\n" +
                "    })();\n" +
                "    </script>\n" +
                "<link rel=\"stylesheet\" href=\"https://csdnimg.cn/release/blogv2/dist/pc/css/blog_code-01256533b5.min.css\">\n" +
                "<link rel=\"stylesheet\" href=\"https://csdnimg.cn/release/blogv2/dist/mdeditor/css/editerView/chart-3456820cac.css\" />\n");
        return string.toString();
    }

}
