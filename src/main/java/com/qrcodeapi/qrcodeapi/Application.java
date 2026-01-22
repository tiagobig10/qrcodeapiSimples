package com.qrcodeapi.qrcodeapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);

		if (true){
			return;
		}

		String text = "abcdef";

		List<Integer> indexes = new ArrayList<>();

		int index = 0;
		int i = 0;
		while (true){
			try {

				System.out.println(text.charAt(index));


				System.out.println(i / text.length() % text.length());


				Thread.sleep(1000);
			} catch (Exception ignored) {

            }
			index++;
			i++;
			if (text.length() == index + 1){
				index = 0;






			}
        }

	}

}
