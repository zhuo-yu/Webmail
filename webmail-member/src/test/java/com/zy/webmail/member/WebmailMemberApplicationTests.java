package com.zy.webmail.member;


import com.zy.webmail.member.entity.MemberEntity;
import com.zy.webmail.member.service.MemberService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.Md5Crypt;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class WebmailMemberApplicationTests {

    @Autowired
    MemberService memberService;
    @Test
    void contextLoads() {
        String s = DigestUtils.md5Hex("12345");
        System.out.println(s);
        String s1 = Md5Crypt.md5Crypt("12345".getBytes());
        System.out.println(s1);
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String s2 = bCryptPasswordEncoder.encode("12345");
        System.out.println(s2);
        /**
         * 827ccb0eea8a706c4c34a16891f84e7b
         * $1$9KslyrQA$mAqEsIF/ZMDF9QFOvt2VG.
         * $2a$10$DpR1qWCFG1tkMVY21C89nONNDYuEb6d/x1DX0aR4TLgcy3N7YgPh2
         */
    }

}
