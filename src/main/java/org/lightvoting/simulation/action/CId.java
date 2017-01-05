package org.lightvoting.simulation.action;

import org.lightjason.agentspeak.action.IBaseAction;
import org.lightjason.agentspeak.common.CPath;
import org.lightjason.agentspeak.common.IPath;
import org.lightjason.agentspeak.language.CRawTerm;
import org.lightjason.agentspeak.language.ITerm;
import org.lightjason.agentspeak.language.execution.IContext;
import org.lightjason.agentspeak.language.execution.fuzzy.CFuzzyValue;
import org.lightjason.agentspeak.language.execution.fuzzy.IFuzzyValue;

import java.text.MessageFormat;
import java.util.List;

public final class CId extends IBaseAction
{

    private final Integer integer;

    public CId(final Integer p_int)
    {
        this.integer = p_int;
    }

    @Override
    public final IPath name()
    {
        return CPath.from( "my/id" );
    }

    @Override
    public final int minimalArgumentNumber()
    {
        return 0;
    }

    @Override
    public final IFuzzyValue<Boolean> execute(final IContext p_context, final boolean p_parallel,
                                              final List<ITerm> p_argument, final List<ITerm> p_return,
                                              final List<ITerm> p_annotation )
    {
        System.out.println(
                MessageFormat.format(
                        "standalone action is called from agent {0} my int is {1}", p_context.agent(), this.integer
                )
        );

        // the action should return a value, you can wrap each Java type into LightJason
        p_return.add( CRawTerm.from( p_context.agent().hashCode() ) );

        // the actions returns a fuzzy-boolean for successful or failing execution
        // the optional second parameter is a fuzzy-value in [0,1] on default it is 1
        return CFuzzyValue.from( true );
    }


}
