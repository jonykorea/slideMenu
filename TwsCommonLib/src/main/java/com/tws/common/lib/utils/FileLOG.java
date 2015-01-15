package com.tws.common.lib.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;
import android.os.Environment;
import android.util.Log;

public class FileLOG {

	public static void writeLog(String str) {

        boolean LOG_START = true;

        String logPath = "tws/log";
        String fileName = "log.txt";

        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+logPath;

		if (LOG_START) {

            File file;
            file = new File(path);
            if(!file.exists()){
                file.mkdirs();
            }
            file = new File(path+File.separator+fileName);

			if (file.exists() == false) {
				try {
					file.createNewFile();
				} catch (IOException e) {

					Log.i("FileLOG writeLog", "writeLog getMessage : " + e.getMessage());
				}
			} else {
				try {
					BufferedWriter bfw = new BufferedWriter(new FileWriter(
                            file, true));

					str = TimeStamp() + " " + str;

					Log.i("FileLOG writeLog", "bfw writeLog : " + str);

					bfw.write(str);
					bfw.write("\n");
					bfw.flush();
					bfw.close();
				} catch (FileNotFoundException e) {
					Log.i("FileLOG writeLog", "writeLog getMessage : " + e.getMessage());

				} catch (IOException e) {
					Log.i("FileLOG writeLog", "writeLog getMessage : " + e.getMessage());
				}
			}
		}
	}

	public static String TimeStamp() {
		String Time = "";

		final Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());

		TimeZone tz = TimeZone.getTimeZone("Asia/Seoul");
		calendar.setTimeZone(tz);

		// 년
		int nYear = calendar.get(Calendar.YEAR);

		// 월
		int nMonth = calendar.get(Calendar.MONTH) + 1;

		// 일
		int nDay = calendar.get(Calendar.DAY_OF_MONTH);

		// 요일
		int nDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

		// 시
		int nHour = calendar.get(Calendar.HOUR_OF_DAY);

		// 분
		int nMin = calendar.get(Calendar.MINUTE);
		
		int nSec = calendar.get(Calendar.SECOND);

		// 날짜 기준 정보.
		Time = "(" + nDayOfWeek + ")" + nYear + "/" + nMonth + "/" + nDay + " "
				+ nHour + ":" + nMin + ":" + nSec;

		return Time;

	}

}
