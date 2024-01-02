package me.limeglass.skore.elements;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import ch.njol.skript.Skript;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import me.limeglass.skore.Skore;
import me.limeglass.skore.Syntax;
import me.limeglass.skore.utils.EnumClassInfo;
import me.limeglass.skore.utils.ReflectionUtil;
import me.limeglass.skore.utils.TypeClassInfo;
import me.limeglass.skore.utils.annotations.Disabled;
import me.limeglass.skore.utils.annotations.ExpressionProperty;
import me.limeglass.skore.utils.annotations.Patterns;
import me.limeglass.skore.utils.annotations.Properties;
import me.limeglass.skore.utils.annotations.PropertiesAddition;
import me.limeglass.skore.utils.annotations.RegisterEnum;
import me.limeglass.skore.utils.annotations.RegisterType;
import me.limeglass.skore.utils.annotations.User;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class Register {
	
	static {
		run : for (Class clazz : ReflectionUtil.getClasses(Skore.getInstance(), Skore.getInstance().getPackageName())) {
			if (!clazz.isAnnotationPresent(Disabled.class)) {
				String[] syntax = null;
				ExpressionType type = ExpressionType.COMBINED;
				if (clazz.isAnnotationPresent(Patterns.class)) {
					syntax = Syntax.register(clazz, ((Patterns)clazz.getAnnotation(Patterns.class)).value());
				} else if (PropertyExpression.class.isAssignableFrom(clazz) && clazz.isAnnotationPresent(Properties.class)) {
					type = ExpressionType.PROPERTY;
					String[] properties = ((Properties)clazz.getAnnotation(Properties.class)).value();
					String additions = (clazz.isAnnotationPresent(PropertiesAddition.class)) ? " " + ((PropertiesAddition) clazz.getAnnotation(PropertiesAddition.class)).value() + " " : " ";
					String input1 = "[the] ", input2 = "";
					if (properties.length > 2 && properties[2] != null) {
						int var = Integer.parseInt(properties[2].substring(1, 2));
						if (var == 1) input1 = properties[2].substring(3, properties[2].length());
						else input2 = properties[2].substring(3, properties[2].length());
					}
					String[] values = new String[]{Skore.getNameplate() + input1 + " " + properties[1] + " (of|from|in)" + additions + "%" + properties[0] + "%", Skore.getNameplate() + input2 + "%" + properties[0] + "%'[s]"  + additions.replace("[the] ", "") + properties[1]};
					syntax = Syntax.register(clazz, values);
					if (syntax == null) Skore.debugMessage("&cThere was an issue registering the syntax for " + clazz.getName() + ". Make sure that the SyntaxToggles.yml is set for this syntax.");
				} else {
					continue run;
				}
				if (clazz.isAnnotationPresent(RegisterEnum.class)) {
					try {
						String user = null;
						String enumType = ((RegisterEnum) clazz.getAnnotation(RegisterEnum.class)).value();
						Class returnType = ((RegisterEnum) clazz.getAnnotation(RegisterEnum.class)).ExprClass();
						if (returnType.equals(String.class)) returnType = ((Expression) clazz.getConstructor().newInstance()).getReturnType();
						if (clazz.isAnnotationPresent(User.class)) user = ((User) clazz.getAnnotation(User.class)).value();
						EnumClassInfo.create(returnType, enumType, user).register();
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
						e.printStackTrace();
					}
				}
				if (clazz.isAnnotationPresent(RegisterType.class)) {
					try {
						String typeName = ((RegisterType) clazz.getAnnotation(RegisterType.class)).value();
						Class returnType = ((RegisterType) clazz.getAnnotation(RegisterType.class)).ExprClass();
						if (returnType.equals(String.class)) returnType = ((Expression) clazz.getConstructor().newInstance()).getReturnType();
						TypeClassInfo.create(returnType, typeName).register();
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
						e.printStackTrace();
					}
				}
				if (syntax != null) {
					if (Effect.class.isAssignableFrom(clazz)) {
						Skript.registerEffect(clazz, syntax);
						Skore.debugMessage("&5Registered Effect " + clazz.getSimpleName() + " (" + clazz.getCanonicalName() + ") with syntax " + Arrays.toString(syntax));
					} else if (Condition.class.isAssignableFrom(clazz)) {
						Skript.registerCondition(clazz, syntax);
						Skore.debugMessage("&5Registered Condition " + clazz.getSimpleName() + " (" + clazz.getCanonicalName() + ") with syntax " + Arrays.toString(syntax));
					} else if (Expression.class.isAssignableFrom(clazz)) {
						if (clazz.isAnnotationPresent(ExpressionProperty.class)) type = ((ExpressionProperty) clazz.getAnnotation(ExpressionProperty.class)).value();
						try {
							Skript.registerExpression(clazz, ((Expression) clazz.getConstructor().newInstance()).getReturnType(), type, syntax);
							Skore.debugMessage("&5Registered Expression " + type.toString() + " " + clazz.getSimpleName() + " (" + clazz.getCanonicalName() + ") with syntax " + Arrays.toString(syntax));
						} catch (IllegalAccessException | IllegalArgumentException | InstantiationException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
							Skore.consoleMessage("&cFailed to register expression " + clazz.getCanonicalName());
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

}
