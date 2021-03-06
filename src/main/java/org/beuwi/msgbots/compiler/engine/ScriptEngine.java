package org.beuwi.msgbots.compiler.engine;

import org.beuwi.msgbots.compiler.api.Api;
import org.beuwi.msgbots.compiler.api.AppData;
import org.beuwi.msgbots.compiler.api.Bridge;
import org.beuwi.msgbots.compiler.api.DataBase;
import org.beuwi.msgbots.compiler.api.Device;
import org.beuwi.msgbots.compiler.api.FileStream;
import org.beuwi.msgbots.compiler.api.GlobalLog;
import org.beuwi.msgbots.compiler.api.ImageDB;
import org.beuwi.msgbots.compiler.api.Log;
import org.beuwi.msgbots.compiler.api.Replier;
import org.beuwi.msgbots.compiler.api.Utils;
import org.beuwi.msgbots.manager.BotManager;
import org.beuwi.msgbots.manager.FileManager;
import org.beuwi.msgbots.manager.LogManager;
import org.beuwi.msgbots.platform.gui.enums.LogType;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.ScriptableObject;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Logger;

public class ScriptEngine
{
	public static HashMap<String, ScriptContainer> container = new HashMap<>();
	public static HashMap<String, Boolean> compiling = new HashMap<>();
	public static ScriptableObject execscope = null;

	protected static void run(String room, String message, String sender, boolean isGroupChat, ImageDB imageDB, String packageName)
	{
		for (String name : FileManager.getBotNames())
		{
			if (!BotManager.getPower(name))
			{
				continue ;
			}

			if (!container.containsKey(name))
			{
				continue ;
			}

			new Thread(() -> callResponder(name, room, message, sender, isGroupChat, imageDB, packageName)).start();
		}
	}

	protected static boolean initialize(String name, boolean isManual, boolean ignoreError)
	{
		LogManager.event("Compile Start : " + name);

		compiling.put(name, true);

		File file = FileManager.getBotScript(name);

		int optimization = 1;

		// Parse Context
		Context context = Context.enter();

		try
		{
			context.setWrapFactory(new PrimitiveWrapFactory());
			context.setLanguageVersion(Context.VERSION_ES6);
			context.setOptimizationLevel(optimization);
		}
		catch (Exception e)
		{
			if (!isManual)
			{
				Context.reportError(e.toString());
			}

			e.printStackTrace();

			return false;
		}

		System.gc();

		ScriptableObject scope = null;
		Script script = null;

		try
		{
			if (container.get(name) != null)
			{
				if (container.get(name).getOnStartCompile() != null)
				{
					container.get(name).getOnStartCompile().call(context, execscope, execscope, new Object[] { });
				}
			}

			scope = (ScriptableObject) context.initStandardObjects(new ImporterTopLevel(context));

			script = context.compileString(FileManager.read(file), file.getName(), 0, null);

			int flags = ScriptableObject.EMPTY;

			ScriptableObject.defineProperty(scope, "Api", 		 ScriptUtils.convert(new Api(scope, name)), flags);
			ScriptableObject.defineProperty(scope, "Device", 	 ScriptUtils.convert(new Device(scope)), flags);
			ScriptableObject.defineProperty(scope, "GlobalLog", ScriptUtils.convert(new GlobalLog(scope)), flags);
			ScriptableObject.defineProperty(scope, "Log", 		 ScriptUtils.convert(new Log(scope, name)), flags);
			ScriptableObject.defineProperty(scope, "DataBase",  ScriptUtils.convert(new DataBase(scope, name)), flags);
			ScriptableObject.defineProperty(scope, "Utils", 	 ScriptUtils.convert(new Utils(scope, name)), flags);

			ScriptableObject.defineClass(scope, AppData.class);
			ScriptableObject.defineClass(scope, Bridge.class);
			ScriptableObject.defineClass(scope, FileStream.class);

			execscope = scope;

			script.exec(context, scope);

			Function onStartCompile = scope.has("onStartCompile", scope) ? (Function) scope.get("onStartCompile", scope) : null;
			Function responder = scope.has("response", scope) ? (Function) scope.get("response", scope) : null;

			container.put(name, new ScriptContainer()
					.setExecScope(scope)
					.setResponder(responder)
					.setOnStartCompile(onStartCompile)
					.setOptimization(optimization)
					.setScope(scope));

			Context.exit();

			compiling.put(name, false);
		}
		catch (Throwable e)
		{
			if (container.get(name) != null)
			{
				container.get(name).setOnStartCompile(null);
			}

			LogManager.error("Compile Error : " + e.toString() + " : " + name);

			compiling.put(name, false);

			if (!isManual)
			{
				if (!ignoreError)
				{
					Context.reportError(e.toString());
				}
			}

			e.printStackTrace();

			return false;
		}

		BotManager.setCompiled(name, true);

		LogManager.event("Compile Success: " + name);

		return true;
	}

	protected static void callResponder(String name, String room, String message, String sender, Boolean isGroupChat, ImageDB imageDB, String packageName)
	{
		ScriptableObject scope = container.get(name).getExecScope();
		Function responder = container.get(name).getResponder();

		Context context = Context.enter();

		final long start = System.currentTimeMillis();

		try
		{
			context.setWrapFactory(new PrimitiveWrapFactory());
			context.setLanguageVersion(Context.VERSION_ES6);
			context.setOptimizationLevel(container.get(name).getOptimization());

			if (responder != null)
			{
				responder.call(context, scope, scope, new Object[] { room, message, sender, isGroupChat, new Replier(), imageDB, packageName });
			}

			Context.exit();
		}
		catch (Throwable e)
		{
			LogManager.error("Runtime Error : " + e.toString() + " : " + name);

			e.printStackTrace();
		}

		final long end = System.currentTimeMillis();

		LogManager.event("Running Time : " + (end - start));
	}
}
