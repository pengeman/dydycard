package org.pengwt.myhome.myhome2.controller;

import com.alibaba.fastjson.JSON;
import lombok.extern.log4j.Log4j2;
import org.pengwt.myhome.myhome2.entity.BookMark;
import org.pengwt.myhome.myhome2.entity.User;
import org.pengwt.myhome.myhome2.service.BookMarkService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Log4j2
@RestController
public class DydyController {
    @Resource
    public BookMarkService bookMarkService;

    @GetMapping(value = "/allbookmarks")
    public String getAllBookMarks() {
        List<BookMark> bms = bookMarkService.getAllBookMarks();
        String jsonString = JSON.toJSONString(bms);
        //System.out.println(jsonString);
        log.debug(jsonString);
        return jsonString;
    }

    @GetMapping(value = "/index")
    /*
    首页，显示下载链接，月卡年卡说明
     */
    public ModelAndView index(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        HttpSession session = request.getSession();

        String footer = "";


        modelAndView.addObject("title", "myhome");

        modelAndView.addObject("footer_var",footer);
        modelAndView.setViewName("index");
        return modelAndView;
    }



    @RequestMapping(value = "/downloadpage")
    public ModelAndView godownloadpage(HttpServletRequest request) {
        // 进入下载页面
        ModelAndView md = new ModelAndView();
        md.setViewName("downloadpage");
        return md;
    }

    //  增加新bookmark项
    @PostMapping(value = "/dosetup")
    public String dosetup(HttpServletRequest request){
        //@RequestParam(name="name") String name , @RequestParam(name="url") String url
          String name = request.getParameter("name");
          String url = request.getParameter("user");
          String username = (String) request.getSession().getAttribute("user");
          User user = bookMarkService.getUserByName(username);
        BookMark bookMark = new BookMark(name,url, user.getId());
        boolean r = this.bookMarkService.saveBookMark(bookMark);
        String s = "新增书签 成功" + r;
        return JSON.toJSONString(s);
    }

    // 删除一条记录
    @PostMapping(value = "/setupdeleteitem")
    public ModelAndView setupdeleteitem(@RequestParam(name="id") long id){
        this.bookMarkService.deleteBookMark(id);
        return new ModelAndView("gosetup");
    }



    @RequestMapping(value = "/gosync")
    public ModelAndView gosync(ModelAndView md) {
        // 进入同步页面，点击开始同步，将服务端数据下载，更新本地数据，再将本地新数据上传服务端
        md.setViewName("sync");
        return md;
    }

    @GetMapping(value = "/gologin")
    public ModelAndView gologin(ModelAndView modelAndView){
        modelAndView.setViewName("login");
        modelAndView.addObject("error","");
        return modelAndView;
    }

    @PostMapping(value = "/dologin")
    public ModelAndView dologin(HttpServletRequest request){
        HttpSession session = request.getSession();
        String username = request.getParameter("username");
        String pwd = request.getParameter("password");
        boolean ok = bookMarkService.loginVerify(username,pwd);
        User user = bookMarkService.getUserByName(username);
        int userid = user.getId();
        ModelAndView modelAndView = new ModelAndView();
        if (ok){
            session.setAttribute("username",username);
            session.setAttribute("userid",userid);
//            modelAndView.addObject("title","myhome");
            modelAndView.setViewName("gohome");
//            List<BookMark> bms = bookMarkService.getAllBookMarks();
//            modelAndView.addObject("rows", bms);
        }else{
            session.removeAttribute("user");
            modelAndView.addObject("title","login");
            modelAndView.addObject("error","用户名及密码错误");
            modelAndView.setViewName("login");
        }
        return modelAndView;
    }

    @PostMapping(value = "/doSignup")
    public ModelAndView doSignup(HttpServletRequest request){
        ModelAndView md = new ModelAndView("login");
        String username = request.getParameter("username");
        String pwd = request.getParameter("password");
        String repwd = request.getParameter("password2");
        if (username == null || username.isEmpty()){
            md.addObject("error","用户名不能空");
            md.setViewName("login");
            return md;
        }
        if (!pwd.equals(repwd)){
            md.addObject("error","用户名口令不一致");
            md.setViewName("login");
            return md;
        }else{
            // todo 注册信息正确，允许注册
            bookMarkService.newUser(username,pwd);
            md.addObject("error","注册成功，再次登录");
            md.setViewName("login");
            return md;
        }
    }
}
