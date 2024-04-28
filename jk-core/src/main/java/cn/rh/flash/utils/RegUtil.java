package cn.rh.flash.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author 
 * @date 2021年11月13日
 */
public class RegUtil {

	private static final String[] PHONE_REGS = new String[] {
		    "^(\\+?0?86\\-?)?1[345789]\\d{9}$",
			"^(\\\\+?213|0)(5|6|7)\\\\d{8}$",
		    "^(!?(\\+?963)|0)?9\\d{8}$",
		    "^(!?(\\+?966)|0)?5\\d{8}$",
		    "^(\\+?1)?[2-9]\\d{2}[2-9](?!11)\\d{6}$",
		    "^(\\+?420)? ?[1-9][0-9]{2} ?[0-9]{3} ?[0-9]{3}$",
		    "^(\\+?49[ \\.\\-])?([\\(]{1}[0-9]{1,6}[\\)])?([0-9 \\.\\-\\/]{3,20})((x|ext|extension)[ ]?[0-9]{1,4})?$",
		    "^(\\+?45)?(\\d{8})$",
		    "^(\\+?30)?(69\\d{8})$",
		    "^(\\+?61|0)4\\d{8}$",
		    "^(\\+?44|0)7\\d{9}$",
		    "^(\\+?852\\-?)?[569]\\d{3}\\-?\\d{4}$",
		    "^(\\+?91|0)?[789]\\d{9}$",
		    "^(\\+?64|0)2\\d{7,9}$",
		    "^(\\+?27|0)\\d{9}$",
		    "^(\\+?26)?09[567]\\d{7}$",
		    "^(\\+?34)?(6\\d{1}|7[1234])\\d{7}$",
		    "^(\\+?358|0)\\s?(4(0|1|2|4|5)?|50)\\s?(\\d\\s?){4,8}\\d$",
		    "^(\\+?33|0)[67]\\d{8}$",
		    "^(\\+972|0)([23489]|5[0248]|77)[1-9]\\d{6}",
		    "^(\\+?36)(20|30|70)\\d{7}$",
		    "^(\\+?39)?\\s?3\\d{2} ?\\d{6,7}$",
		    "^(\\+?81|0)\\d{1,4}[ \\-]?\\d{1,4}[ \\-]?\\d{4}$",
		    "^(\\+?6?01){1}(([145]{1}(\\-|\\s)?\\d{7,8})|([236789]{1}(\\s|\\-)?\\d{7}))$",
		    "^(\\+?47)?[49]\\d{7}$",
		    "^(\\+?32|0)4?\\d{8}$",
		    "^(\\+?47)?[49]\\d{7}$",
		    "^(\\+?48)? ?[5-8]\\d ?\\d{3} ?\\d{2} ?\\d{2}$",
		    "^(\\+?55|0)\\-?[1-9]{2}\\-?[2-9]{1}\\d{3,4}\\-?\\d{4}$",
		    "^(\\+?351)?9[1236]\\d{7}$",
		    "^(\\+?7|8)?9\\d{9}$",
		    "^(\\+3816|06)[- \\d]{5,9}$",
		    "^(\\+?90|0)?5\\d{9}$",
		    "^(\\+?84|0)?((1(2([0-9])|6([2-9])|88|99))|(9((?!5)[0-9])))([0-9]{7})$",
		    "^(\\+?886\\-?|0)?9\\d{8}$"
	};
	
//	private static final String PASSWORD_REG = "^(?=.*[0-9].*)(?=.*[A-Z].*)(?=.*[a-z].*).{8,12}$";
//	private static final String PASSWORD_REG = "^(?=.*[0-9].*)(?=.*[A-Z].*)(?=.*[a-z].*).{6,12}$";
	private static final String PASSWORD_REG = "^[A-Za-z0-9]{6,12}$";

	private static final String PAY_PASSWORD_REG = "^[0-9]{6,6}$";

	private static final String COUNTRY_CODE_REG = "^[0-9]*$";

//	private static final String ACCOUNT_CODE_REG = "^(?!.*(?<re1>[0-9])\\k<re1>{3})[0-9]+$"; // 账号出现三个以上的连续数字 不通过校验
	private static final String ACCOUNT_CODE_REG = "^([0-9]{1}[0-9]{0,14})$"; // 账号出现三个以上的连续数字 不通过校验


	private static final String test =  "^[0-9 +]+$";
	
	private static final String EMAIL_REG = "^([a-z0-9A-Z_]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

	private static final String CH_PHONE_REG="^1(3[0-9]|4[57]|5[0-35-9]|6[6]|7[0135678]|8[0-9]|9[89])\\d{8}$";

	public static boolean isCHPhone(String countryCode,String phone){
		if ("86".equals(countryCode)){
			if (StringUtils.isNotEmpty(phone)){
				return phone.matches(CH_PHONE_REG);
			}
		}
		return false;
	}
	
	public static boolean isPhone(String countryCode, String phone) {
		if (StringUtils.isNotEmpty(phone)){
			String completePhone = StringUtils.isEmpty(countryCode) ? phone : (countryCode + phone);
			for(String reg : PHONE_REGS) {
				if(completePhone.matches(reg)) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isValidAccount(String account) {
		if(StringUtils.isEmpty(account)) {
			return false;
		}
		return account.matches(ACCOUNT_CODE_REG);
	}
	
	public static boolean isValidPayPassword(String password) {
		if(StringUtils.isEmpty(password)) {
			return false;
		}
		return password.matches(PAY_PASSWORD_REG);
	}

	public static boolean isValidCountryCode(String code) {
		if(StringUtils.isEmpty(code)) {
			return false;
		}
		return code.matches(COUNTRY_CODE_REG);
	}
	
	public static boolean isEmail(String email) {
		if (StringUtils.isNotEmpty(email)) {
			return email.matches(EMAIL_REG);
		}else
			return false;
	}
	
	public static boolean isValidPassword(String password) {
		if(StringUtils.isEmpty(password)) {
			return false;
		}
		return password.matches(PASSWORD_REG);
	}
	
	public static boolean isPlusAndNum(String number) {
		if (StringUtils.isEmpty(number)) {
			return false;
		}
		return number.matches(test);
	}

	
}