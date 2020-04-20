package com.zy.webmail.member;


import com.zy.webmail.menber.entity.MemberEntity;
import com.zy.webmail.menber.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class WebmailMemberApplicationTests {

    @Autowired
    MemberService memberService;
    @Test
    void contextLoads() {
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setUsername("张三");
        boolean save = memberService.save(memberEntity);
        System.out.println("插入情况:"+save);
    }

}
