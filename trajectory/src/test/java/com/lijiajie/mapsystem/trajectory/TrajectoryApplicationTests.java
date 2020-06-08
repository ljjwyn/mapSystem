package com.lijiajie.mapsystem.trajectory;

import com.lijiajie.mapsystem.trajectory.util.JedisUtil;
import com.lijiajie.mapsystem.trajectory.util.SchedulDistance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.Async;

import java.util.*;

@SpringBootTest
class TrajectoryApplicationTests {

	@Autowired
	JedisUtil jedisUtil;

	/**
	 * @describe 单元测试jedis存List中map对象，为电子栅栏的坐标redis缓存做准备
	 */
	@Test
	void contextLoads() {
		List<Map<String,Double>> testList = new ArrayList<>();
		Map<String,Double> m1 = new HashMap<>();
		m1.put("lng",132.123);
		m1.put("lat",32.423);
		Map<String,Double> m2 = new HashMap<>();
		m2.put("lng",121.423);
		m2.put("lat",39.422);
		testList.add(m1);
		testList.add(m2);
		jedisUtil.setList("fencing",testList);
	}
	@Test
	void getFencingTest() {
		List<Map<String,Double>> testList = jedisUtil.getList("fencing");
		System.out.println(1);

	}
	@Test
	void testSet(){
		jedisUtil.sadd("ljj","21180231335");
		jedisUtil.sadd("ljj","21180231335");
		jedisUtil.sadd("ljj","21180231335");
		jedisUtil.sadd("ljj","21180231336");
		jedisUtil.sadd("ljj","21180231336");
		jedisUtil.sadd("ljj","21180231337");
		Set<String> SD = jedisUtil.smembers("ljj");
		System.out.println(jedisUtil.sismember("ljj","21180231333"));
		System.out.println(SD.toString());
	}

}
