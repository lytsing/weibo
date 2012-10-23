package org.lytsing.android.weibo.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

public class TextUtil {
	
	/**
	 * 将text中@某人的字体加亮，匹配的表情文字以表情显示
	 * @param text
	 * @param context
	 * @return
	 */
	public static void formatContent(TextView textView) {
		SpannableString spannableString = new SpannableString(textView.getText());
		/*
		 * @[^\\s:：]+[:：\\s] 匹配@某人  \\[[^0-9]{1,4}\\] 匹配表情
		 */
		Pattern pattern = Pattern.compile("@[^\\s:：]+[:：\\s]|\\[[^0-9]{1,4}\\]");
		Matcher matcher = pattern.matcher(spannableString);
		while (matcher.find()) {
			String match=matcher.group();
			if(match.startsWith("@")){ //@某人，加亮字体
				spannableString.setSpan(new ForegroundColorSpan(0xff0077ff),
						matcher.start(), matcher.end(),
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}

	}

}
