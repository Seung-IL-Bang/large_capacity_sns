package com.example.fastcampusmysql.util;

import com.example.fastcampusmysql.domain.member.entity.Member;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;

public class MemberFixtureFactory {

    /* Default */
    static public Member create() {
        EasyRandomParameters param = new EasyRandomParameters();
        return new EasyRandom(param).nextObject(Member.class);
    }
    static public Member create(Long seed) {
        EasyRandomParameters param = new EasyRandomParameters().seed(seed);
        return new EasyRandom(param).nextObject(Member.class);
    }
}
