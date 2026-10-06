package org.drools.core.phreak;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.drools.core.common.InternalWorkingMemory;
import org.junit.jupiter.api.Test;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

class PhreakRuleTerminalNodeAutoFocusTest
{
    @RequiredArgsConstructor
    @Getter
    @Setter
    public static class TestFact
    {
        private final String name;
        private final boolean autoFocusTarget;
        private String state = "PENDING";
        private boolean priorityHandled;
    }

    private static final String PRIMARY_GROUP = "primaryGroup";
    private static final String RULES = """
                                        package org.drools.core.phreak;
                                        
                                        import org.drools.core.phreak.PhreakRuleTerminalNodeAutoFocusTest.TestFact;
                                        
                                        global java.util.List fired;
                                        
                                        rule "Prepare source fact"
                                            ruleflow-group "primaryGroup"
                                        when
                                            $source : TestFact(name == "source", state == "PENDING")
                                        then
                                            modify($source) { setState("READY") };
                                        end
                                        
                                        rule "Auto-focus target group"
                                            ruleflow-group "autoFocusGroup"
                                            auto-focus true
                                        when
                                            $source : TestFact(autoFocusTarget == false, state == "READY", priorityHandled == false)
                                            $target : TestFact(autoFocusTarget == true, state == "PENDING", priorityHandled == false)
                                        then
                                            modify($source) {
                                                setState("PENDING"),
                                                setPriorityHandled(true)
                                            };
                                            modify($target) {
                                                setState("READY"),
                                                setPriorityHandled(true)
                                            };
                                            fired.add("auto-focus");
                                        end
                                        
                                        rule "Process ready fact"
                                            ruleflow-group "primaryGroup"
                                        when
                                            $fact : TestFact(state == "READY")
                                        then
                                            modify($fact) { setState("COMPLETE") };
                                            fired.add("process-" + $fact.getName());
                                        end""";

    @Test
    void autoFocusedGroupPreemptsPreviouslyFocusedGroup()
    {
        final KieBase kieBase = new KieHelper().addContent(RULES, ResourceType.DRL).build();
        final List<String> firedRules = new ArrayList<>();

        try (final KieSession kieSession = kieBase.newKieSession())
        {
            kieSession.setGlobal("fired", firedRules);
            kieSession.insert(new TestFact("source", false));
            kieSession.insert(new TestFact("target", true));
            ((InternalWorkingMemory) kieSession).getAgenda().activateRuleFlowGroup(PRIMARY_GROUP);

            kieSession.fireAllRules();
        }

        assertEquals(List.of("auto-focus", "process-target", "process-source"), firedRules);
    }
}
