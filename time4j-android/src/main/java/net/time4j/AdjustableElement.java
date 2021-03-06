/*
 * Licensed by the author of Time4J-project.
 *
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership. The copyright owner
 * licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package net.time4j;

import net.time4j.engine.ChronoElement;


/**
 * <p>Extends a chronological element by some standard ways of
 * manipulation. </p>
 *
 * @param   <V> generic type of element values
 * @param   <T> generic type of target entity an operator is applied to
 * @author  Meno Hochschild
 */
/*[deutsch]
 * <p>Erweitert ein chronologisches Element um diverse
 * Standardmanipulationen. </p>
 *
 * @param   <V> generic type of element values
 * @param   <T> generic type of target entity an operator is applied to
 * @author  Meno Hochschild
 */
public interface AdjustableElement<V, T>
    extends ZonalElement<V> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Sets any local entity to given new value of this element. </p>
     *
     * @param   value   new element value
     * @return  operator directly applicable also on {@code PlainTimestamp}
     * @since   2.0
     * @see     net.time4j.engine.ChronoEntity#with(ChronoElement,Object)
     *          ChronoEntity.with(ChronoElement, V)
     */
    /*[deutsch]
     * <p>Setzt eine beliebige Entit&auml;t auf den angegebenen Wert. </p>
     *
     * @param   value   new element value
     * @return  operator directly applicable also on {@code PlainTimestamp}
     * @since   2.0
     * @see     net.time4j.engine.ChronoEntity#with(ChronoElement,Object)
     *          ChronoEntity.with(ChronoElement, V)
     */
    ElementOperator<T> newValue(V value);

    /**
     * <p>Sets any local entity to the minimum of this element. </p>
     *
     * @return  operator directly applicable also on {@code PlainTimestamp}
     */
    /*[deutsch]
     * <p>Setzt eine beliebige Entit&auml;t auf das Elementminimum. </p>
     *
     * @return  operator directly applicable also on {@code PlainTimestamp}
     */
    ElementOperator<T> minimized();

    /**
     * <p>Sets any local entity to the maximum of this element. </p>
     *
     * @return  operator directly applicable also on {@code PlainTimestamp}
     */
    /*[deutsch]
     * <p>Setzt eine beliebige Entit&auml;t auf das Elementmaximum. </p>
     *
     * @return  operator directly applicable also on {@code PlainTimestamp}
     */
    ElementOperator<T> maximized();

    /**
     * <p>Adjusts any local entity such that this element gets the previous
     * value. </p>
     *
     * <p>The operator throws a {@code ChronoException} if there is no
     * base unit available for this element. </p>
     *
     * @return  operator directly applicable also on {@code PlainTimestamp}
     *          and requiring a base unit in given chronology for decrementing
     * @see     net.time4j.engine.TimeAxis#getBaseUnit(ChronoElement)
     */
    /*[deutsch]
     * <p>Passt eine beliebige Entit&auml;t so an, da&szlig; dieses Element
     * den vorherigen Wert bekommt. </p>
     *
     * <p>Der Operator wirft eine {@code ChronoException}, wenn er auf einen
     * Zeitpunkt angewandt wird, dessen Zeitachse keine Basiseinheit zu diesem
     * Element kennt. </p>
     *
     * @return  operator directly applicable also on {@code PlainTimestamp}
     *          and requiring a base unit in given chronology for decrementing
     * @see     net.time4j.engine.TimeAxis#getBaseUnit(ChronoElement)
     */
    ElementOperator<T> decremented();

    /**
     * <p>Adjusts any local entity such that this element gets the next
     * value. </p>
     *
     * <p>The operator throws a {@code ChronoException} if there is no
     * base unit available for this element. </p>
     *
     * @return  operator directly applicable also on {@code PlainTimestamp}
     *          and requiring a base unit in given chronology for incrementing
     * @see     net.time4j.engine.TimeAxis#getBaseUnit(ChronoElement)
     */
    /*[deutsch]
     * <p>Passt eine beliebige Entit&auml;t so an, da&szlig; dieses Element
     * den n&auml;chsten Wert bekommt. </p>
     *
     * <p>Der Operator wirft eine {@code ChronoException}, wenn er auf einen
     * Zeitpunkt angewandt wird, dessen Zeitachse keine Basiseinheit zu diesem
     * Element kennt. </p>
     *
     * @return  operator directly applicable also on {@code PlainTimestamp}
     *          and requiring a base unit in given chronology for incrementing
     * @see     net.time4j.engine.TimeAxis#getBaseUnit(ChronoElement)
     */
    ElementOperator<T> incremented();

    /**
     * <p>Rounds down an entity by setting all child elements to minimum. </p>
     *
     * <p>Many elements are organized by parent-child-relations. The most important dependency chain is:
     * YEAR -&gt; MONTH -&gt; DAY_OF_MONTH -&gt; HOUR_OF_DAY -&gt; MINUTE -&gt; SECOND -&gt; NANO_OF_SECOND.
     * If there is no child element then this operator will not do anything (no-op). Example: </p>
     *
     * <pre>
     *     PlainDate date = PlainDate.of(2016, 11, 24);
     *     // DAY_OF_WEEK has no time element as child in context of plain calendar dates
     *     System.out.println(date.with(DAY_OF_WEEK.atFloor())); // 2016-11-24
     *
     *     PlainTimestamp tsp = date.atTime(20, 45);
     *     // DAY_OF_WEEK has now child elements which can be set to zero
     *     System.out.println(tsp.with(DAY_OF_WEEK.atFloor())); // 2016-11-24T00
     * </pre>
     *
     * @return  operator directly applicable on local types without timezone
     */
    /*[deutsch]
     * <p>Rundet eine Entit&auml;t ab, indem alle Kindselemente dieses Elements auf ihr Minimum gesetzt werden. </p>
     *
     * <p>Viele Elemente sind in Eltern-Kind-Relationen organisiert. Die wichtigste Abh&auml;ngigkeitskette ist:
     * YEAR -&gt; MONTH -&gt; DAY_OF_MONTH -&gt; HOUR_OF_DAY -&gt; MINUTE -&gt; SECOND -&gt; NANO_OF_SECOND.
     * Wenn es kein Kindselement gibt, wird dieser Operator nichts tun (no-op). Beispiel: </p>
     *
     * <pre>
     *     PlainDate date = PlainDate.of(2016, 11, 24);
     *     // DAY_OF_WEEK hat kein Uhrzeitelement im Kontext eines reinen Kalenderdatums
     *     System.out.println(date.with(DAY_OF_WEEK.atFloor())); // 2016-11-24
     *
     *     PlainTimestamp tsp = date.atTime(20, 45);
     *     // DAY_OF_WEEK hat jetzt Kindselemente, die genullt werden k&ouml;nnen
     *     System.out.println(tsp.with(DAY_OF_WEEK.atFloor())); // 2016-11-24T00
     * </pre>
     *
     * @return  operator directly applicable also on {@code PlainTimestamp}
     */
    ElementOperator<T> atFloor();

    /**
     * <p>Rounds up an entity by setting all child elements to maximum. </p>
     *
     * <p>Many elements are organized by parent-child-relations. The most important dependency chain is:
     * YEAR -&gt; MONTH -&gt; DAY_OF_MONTH -&gt; HOUR_OF_DAY -&gt; MINUTE -&gt; SECOND -&gt; NANO_OF_SECOND.
     * If there is no child element then this operator will not do anything (no-op). Example: </p>
     *
     * <pre>
     *     PlainDate date = PlainDate.of(2016, 11, 24);
     *     // DAY_OF_WEEK has no time element as child in context of plain calendar dates
     *     System.out.println(date.with(DAY_OF_WEEK.atCeiling())); // 2016-11-24
     *
     *     PlainTimestamp tsp = date.atTime(20, 45);
     *     // DAY_OF_WEEK has now child elements which can be all maximized
     *     System.out.println(tsp.with(DAY_OF_WEEK.atCeiling())); // 2016-11-24T23:59:59,999999999
     * </pre>
     *
     * @return  operator directly applicable on local types without timezone
     */
    /*[deutsch]
     * <p>Rundet eine Entit&auml;t auf, indem alle Kindselemente dieses Elements auf ihr Maximum gesetzt werden. </p>
     *
     * <p>Viele Elemente sind in Eltern-Kind-Relationen organisiert. Die wichtigste Abh&auml;ngigkeitskette ist:
     * YEAR -&gt; MONTH -&gt; DAY_OF_MONTH -&gt; HOUR_OF_DAY -&gt; MINUTE -&gt; SECOND -&gt; NANO_OF_SECOND.
     * Wenn es kein Kindselement gibt, wird dieser Operator nichts tun (no-op). Beispiel: </p>
     *
     * <pre>
     *     PlainDate date = PlainDate.of(2016, 11, 24);
     *     // DAY_OF_WEEK hat kein Uhrzeitelement im Kontext eines reinen Kalenderdatums
     *     System.out.println(date.with(DAY_OF_WEEK.atCeiling())); // 2016-11-24
     *
     *     PlainTimestamp tsp = date.atTime(20, 45);
     *     // DAY_OF_WEEK hat jetzt Kindselemente, die alle maximiert werden k&ouml;nnen
     *     System.out.println(tsp.with(DAY_OF_WEEK.atCeiling())); // 2016-11-24T23:59:59,999999999
     * </pre>
     *
     * @return  operator directly applicable also on {@code PlainTimestamp}
     */
    ElementOperator<T> atCeiling();

}
