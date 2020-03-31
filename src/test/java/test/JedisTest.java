package test;

import mytest.utils.MyUtils;
import redis.clients.jedis.Jedis;

public class JedisTest {
    public static void main(String[] args) {

//        Jedis jedis = new Jedis("localhost");

        Jedis jedis = MyUtils.getJedis();
        jedis.set("test","123");

        String test = jedis.get("test");
        System.out.println(test);
    }
}
