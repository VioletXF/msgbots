package org.beuwi.msgbots.platform.util;

public class ProgramUtils
{
	public static String getIDType(String id)
	{
		return id.split("@")[1].split("::")[0];
	}

	public static String getIDName(String id)
	{
		return id.split("@")[1].split("::")[1];
	}
}