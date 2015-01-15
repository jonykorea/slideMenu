package com.tws.common.lib.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;

public class TimeUtil {

	private static SimpleDateFormat sdf;
	private static Calendar calendar;

	public static String getCurrentSimpleDateFormat(String pattern) {
		sdf = new SimpleDateFormat(pattern, Locale.KOREA);
		sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
		Date currentTime = new Date();
		String mTime = sdf.format(currentTime);
		return mTime;
	}

	public static String getNewSimpleDateFormat(String pattern, String time) {

		String mTime = "";

		if (TextUtils.isEmpty(pattern) || TextUtils.isEmpty(time))
			return mTime;

		sdf = new SimpleDateFormat(pattern, Locale.KOREA);
		sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

		calendar = Calendar.getInstance();
		calendar.setTimeZone(sdf.getTimeZone());
		calendar.setTimeInMillis(Long.parseLong(time) * 1000L);

		mTime = sdf.format(calendar.getTime());
		return mTime;
	}

	public void setKoreaSimpleDateFormat(String pattern) {
		sdf = new SimpleDateFormat(pattern, Locale.KOREA);
		sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

		calendar = Calendar.getInstance();
		calendar.setTimeZone(sdf.getTimeZone());
	}

	public static String getTimeSetSimpleDateFormat(String time) {

		calendar.setTimeInMillis(Long.parseLong(time) * 1000L);

		return sdf.format(calendar.getTime());
	}

	public static String getSimpleDateFormatAMPM(String time) {

		String mTime = "";

		mTime = getNewSimpleDateFormat("yyyy D a hh시 mm분", time);

		return mTime;
	}

    public static String getSimpleDateFormatYMD(String time) {

        String mTime = "";

        mTime = getNewSimpleDateFormat("yyyyMMdd", time);

        return mTime;
    }

    public static String getSimpleDateFormatMMDD(String time) {

        String mTime = "";

        mTime = getNewSimpleDateFormat("MM월 dd일", time);

        return mTime;
    }

	public static String getDateYYYYMMDD(int gap) {

		Calendar cal = Calendar.getInstance();

		cal.add(cal.DATE, gap);

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd",
				Locale.KOREA);
		dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

		return dateFormat.format(cal.getTime());
	}

	public static long getUpdateTimeToUnixTime(String time) {

		sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREA);
		sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

		long a = -1;

		if (time.length() < 14) {
			return a;
		}

		try {
			a = sdf.parse(time).getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return a;
	}

	/*
	 * 포멧 규칙. - 해당 데이터가 오늘 데이터인 경우 – 오늘 오전/오후 HH시 MM분 - 해당 데이터가 어제 데이터인 경우 - 어제
	 * 오전/오후 HH시 MM분 - 해당 데이터가 2일전~올해의 데이터인 경우 – MM월 DD일 오전/오후 HH시 MM분 - 해당 데이터가
	 * 올해 이전의 데이터인 경우 – YYYY년 MM월 DD일 오전/오후 HH시 MM분
	 */
	public static String getUpdateTimeInfo(String strUpdateTime) {
		String strTime = "";

		long unixTime = System.currentTimeMillis() / 1000L;

		String updateTime = "";

		updateTime = getNewSimpleDateFormat("yyyy D MM dd a hh mm",
				strUpdateTime);

		String nowTime = "";

		nowTime = getNewSimpleDateFormat("yyyy D MM dd a hh mm",
				Long.toString(unixTime));

		String[] updateTimeSplited = updateTime.split(" ");

		String[] nowTimeSplited = nowTime.split(" ");

		int nYear = Integer.parseInt(updateTimeSplited[0]);

		int nMonth = Integer.parseInt(updateTimeSplited[2]);
		int nDate = Integer.parseInt(updateTimeSplited[3]);

		String AmPm = updateTimeSplited[4];

		int nHH = Integer.parseInt(updateTimeSplited[5]);
		int nMM = Integer.parseInt(updateTimeSplited[6]);

		if (updateTimeSplited[0].equalsIgnoreCase(nowTimeSplited[0])) {

			int NowDay = Integer.parseInt(nowTimeSplited[1]);
			int UpdateDay = Integer.parseInt(updateTimeSplited[1]);

			// 올해.
			if (NowDay == UpdateDay) {
				// 오늘 데이터인 경우 – 오늘 오전/오후 HH시 MM분
	
				strTime = "오늘 " + AmPm + " " + nHH + "시 ";
				
				strTime += (nMM>0)?nMM + "분":"";

			} else if (NowDay == (UpdateDay + 1)) {
				// 어제 데이터인 경우 - 어제 오전/오후 HH시 MM분
				strTime = "어제 " + AmPm + " " + nHH + "시 ";
				
				strTime += (nMM>0)?nMM + "분":"";
			} else {
				// 2일전~올해의 데이터인 경우 – MM월 DD일 오전/오후 HH시 MM분
				strTime = nMonth + "월 " + nDate + "일 " + AmPm + " " + nHH
						+ "시 ";
				
				strTime += (nMM>0)?nMM + "분":"";
			}
		} else {
			// 올해 이전의 데이터인 경우 – YYYY년 MM월 DD일 오전/오후 HH시 MM분
			strTime = nYear + "년 " + nMonth + "월 " + nDate + "일 " + AmPm + " "
					+ nHH + "시 ";
			
			strTime += (nMM>0)?nMM + "분":"";

		}

		return strTime;
	}


    public static String getSoulBrownOrderDateInfo(String strUpdateTime) {
        String strTime = "";

        long unixTime = System.currentTimeMillis() / 1000L;

        String updateTime = "";

        updateTime = getNewSimpleDateFormat("yyyy D MM dd a hh mm",
                strUpdateTime);

        String nowTime = "";

        nowTime = getNewSimpleDateFormat("yyyy D MM dd a hh mm",
                Long.toString(unixTime));

        String[] updateTimeSplited = updateTime.split(" ");

        String[] nowTimeSplited = nowTime.split(" ");

        int nYear = Integer.parseInt(updateTimeSplited[0]);

        int nMonth = Integer.parseInt(updateTimeSplited[2]);
        int nDate = Integer.parseInt(updateTimeSplited[3]);

        String AmPm = updateTimeSplited[4];

        int nHH = Integer.parseInt(updateTimeSplited[5]);
        int nMM = Integer.parseInt(updateTimeSplited[6]);

        if (updateTimeSplited[0].equalsIgnoreCase(nowTimeSplited[0])) {

            int NowDay = Integer.parseInt(nowTimeSplited[1]);
            int UpdateDay = Integer.parseInt(updateTimeSplited[1]);

            // 올해.
            if (NowDay == UpdateDay) {
                // 오늘 데이터인 경우 – 오늘

                strTime = "오늘";

            } else if (NowDay == (UpdateDay + 1)) {
                // 어제 데이터인 경우 - 어제
                strTime = "어제";

            } else {
                // 2일전~올해의 데이터인 경우 – MM월 DD일
                strTime = nMonth + "월 " + nDate + "일";
            }
        } else {
            // 올해 이전의 데이터인 경우 – YYYY년 MM월 DD일
            strTime = nYear + "년 " + nMonth + "월 " + nDate + "일";

        }

        return strTime;
    }

}
