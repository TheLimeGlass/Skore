package me.limeglass.skore.lang;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.event.Event;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import me.limeglass.skore.Skore;
import me.limeglass.skore.Syntax;
import me.limeglass.skore.utils.Utils;
import me.limeglass.skore.utils.annotations.AllChangers;
import me.limeglass.skore.utils.annotations.Changers;
import me.limeglass.skore.utils.annotations.Multiple;
import me.limeglass.skore.utils.annotations.Properties;
import me.limeglass.skore.utils.annotations.Settable;

public abstract class SkorePropertyExpression<F, T> extends PropertyExpression<F, T> {

	private List<Object> values = new ArrayList<Object>();
	protected Set<T> collection = new HashSet<T>();
	protected ExpressionData expressions;
	protected Class<T> expressionClass;
	
	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends T> getReturnType() {
		if (expressionClass == null) expressionClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
		return expressionClass;
	}
	
	public String[] getSyntax() {
		return Syntax.get(getClass().getSimpleName());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
		setExpr((Expression<? extends F>) expressions[0]);
		if (expressions != null && getSyntax() != null) this.expressions = new ExpressionData(expressions, getSyntax()[0]);
		return true;
	}

	protected String getPropertyName() {
		return (getClass().isAnnotationPresent(Properties.class)) ? ((Properties) getClass().getAnnotation(Properties.class)).value()[1] : null;
	}
	
	@Override
	public String toString(Event event, boolean debug) {
		String modSyntax = Syntax.isModified(getClass()) ? "Modified syntax: " + Arrays.toString(getSyntax()) : Arrays.toString(getSyntax());
		if (event != null) Skore.debugMessage(getClass().getSimpleName() + " - " + modSyntax + " (" + event.getEventName() + ")" + " Data: " + Arrays.toString(values.toArray()));
		return Skore.getNameplate() + getClass().getSimpleName() + ": the " + getPropertyName() + " (of|from) " + getExpr().toString(event, debug);
	}
	
	@Override
	public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
		Class<?>[] returnable = (getClass().isAnnotationPresent(Multiple.class)) ? CollectionUtils.array(Utils.getArrayClass(expressionClass)) : CollectionUtils.array(expressionClass);
		if (getClass().isAnnotationPresent(Settable.class)) returnable = getClass().getAnnotation(Settable.class).value();
		if (getClass().isAnnotationPresent(AllChangers.class)) return returnable;
		if (!getClass().isAnnotationPresent(Changers.class)) return null;
		return (Arrays.asList(getClass().getAnnotation(Changers.class).value()).contains(mode)) ? returnable : null;
	}
	
	protected Boolean isNull(Event event) {
		if (getExpr() == null) return true;
		if (getExpr().isSingle() && getExpr().getSingle(event) == null) {
			Skore.debugMessage("The property expression was null: " + getExpr().toString(event, true));
			return true;
		} else if (getExpr().getAll(event).length == 0 || getExpr().getAll(event) == null) {
			Skore.debugMessage("The property expression was null: " + getExpr().toString(event, true));
			return true;
		}
		return false;
	}

}