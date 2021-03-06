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

package net.time4j.calendar.astro;

import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.base.MathUtils;
import net.time4j.engine.CalendarDate;
import net.time4j.engine.EpochDays;
import net.time4j.scale.LeapSeconds;
import net.time4j.scale.TimeScale;
import net.time4j.tz.ZonalOffset;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;


/**
 * <p>The Julian day is the Julian day number for the preceding noon plus the fraction of the day
 * (counting 86400 seconds) since that instant. </p>
 *
 * <p>A {@link net.time4j.engine.EpochDays#JULIAN_DAY_NUMBER Julian day number} is defined as continuous
 * number associated with the solar day and is zero at Greenwich mean noon on 1st of January 4713 BC.
 * <strong>Important:</strong> Julian days are mainly used by astronomers and have nothing to do with
 * the Julian calendar. See also the authoritative
 * <a href="https://www.iau.org/static/resolutions/IAU1997_French.pdf">definition of IAU</a>. </p>
 *
 * <p>Note: The range of this class is limited to the year range of roughly -2000/+3000 because the precision
 * will strongly decrease beyond those limits. </p>
 *
 * @author  Meno Hochschild
 * @since   3.33/4.28
 */
/*[deutsch]
 * <p>Der julianische Tag ist die julianische Tagesnummer f&uum;r den Mittag des vorangehenden Tages
 * plus der fraktionale Tagesteil seit diesem Mittag (1 Tag = 86400 Sekunden. </p>
 *
 * <p>Eine {@link net.time4j.engine.EpochDays#JULIAN_DAY_NUMBER julianische Tagesnummer} ist eine
 * fortlaufende mit einem Sonnentag verkn&uuml;pfte Nummer, die gleich 0 am ersten Januar
 * 4713 v.U.Z. um 12 Uhr auf dem L&auml;ngengrad von Greenwich ist. <strong>Wichtig:</strong>
 * Julianische Tage werden vorwiegend von Astronomen verwendet und haben nichts mit dem julianischen
 * Kalender zu tun. Siehe auch die offizielle
 * <a href="https://www.iau.org/static/resolutions/IAU1997_French.pdf">Definition der IAU</a>. </p>
 *
 * <p>Hinweis: Der Wertebereich dieser Klasse ist auf die Jahresspanne von ungef&auml;hr -2000/+3000 beschr&auml;nkt,
 * weil jenseits dieser Grenzen die Genauigkeit stark abnimmt. </p>
 *
 * @author  Meno Hochschild
 * @since   3.33/4.28
 */
public final class JulianDay
    implements Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final int MRD = 1000000000;
    private static final int DAY_IN_SECONDS = 86400;
    private static final long OFFSET_1970 = (2451545 - (2000 - 1970) * 365 - 8) * 86400L + 43200;
    private static final long OFFSET_1972 = (2451545 - (2000 - 1972) * 365 - 8) * 86400L + 43200;

    /**
     * The minimum value which corresponds roughly to the year {@code -2000}.
     */
    /*[deutsch]
     * Der Minimumwert, der ungef&auml;hr dem Jahr {@code -2000} entspricht.
     */
    public static final double MIN = 990575.0;

    /**
     * The maximum value which corresponds roughly to the year {@code +3000}.
     */
    /*[deutsch]
     * Der Maximumwert, der ungef&auml;hr dem Jahr {@code +3000} entspricht.
     */
    public static final double MAX = 2817152.0;

    private static final long serialVersionUID = 486345450973062467L;

    //~ Instanzvariablen --------------------------------------------------

    /**
     * @serial  the floating decimal value
     */
    /*[deutsch]
     * @serial  die Flie&szlig;kommazahl
     */
    private final double value;

    /**
     * @serial  the underlying time scale
     */
    /*[deutsch]
     * @serial  die zugrundeliegende Zeitskala
     */
    private final TimeScale scale;

    //~ Konstruktoren -----------------------------------------------------

    private JulianDay(
        double value,
        TimeScale scale
    ) {
        super();

        check(value, scale);

        this.value = value;
        this.scale = scale;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Creates a Julian day on the time scale {@link TimeScale#TT},
     * sometimes also called <em>Julian Ephemeris Day</em>. </p>
     *
     * <p>This kind of Julian day represents the actual astronomical standard. The time
     * TT-2000-01-01T12:00Z corresponds to JD(TT)2451545.0 </p>
     *
     * @param   value   floating decimal value in range {@code 990575.0 - 2817152.0}
     * @return  JulianDay
     * @throws  IllegalArgumentException if given value is not a finite number or out of range
     */
    /*[deutsch]
     * <p>Erzeugt einen julianischen Tag auf der Zeitskala {@link TimeScale#TT},
     * manchmal auch <em>Julian Ephemeris Day</em> genannt. </p>
     *
     * <p>Diese Art des julianischen Tages repr&auml;sentiert den aktuellen astronomischen Standard.
     * Die Zeit TT-2000-01-01T12:00Z entspricht JD(TT)2451545.0 </p>
     *
     * @param   value   floating decimal value in range {@code 990575.0 - 2817152.0}
     * @return  JulianDay
     * @throws  IllegalArgumentException if given value is not a finite number or out of range
     */
    public static JulianDay ofEphemerisTime(double value) {

        return new JulianDay(value, TimeScale.TT);

    }

    /**
     * <p>Creates a Julian day on the time scale {@link TimeScale#TT},
     * sometimes also called <em>Julian Ephemeris Day</em>. </p>
     *
     * <p>This kind of Julian day represents the actual astronomical standard. The time
     * TT-2000-01-01T12:00Z corresponds to JD(TT)2451545.0 </p>
     *
     * @param   moment      corresponding moment
     * @return  JulianDay
     * @throws  IllegalArgumentException if the Julian day of moment is not in supported range
     */
    /*[deutsch]
     * <p>Erzeugt einen julianischen Tag auf der Zeitskala {@link TimeScale#TT},
     * manchmal auch <em>Julian Ephemeris Day</em> genannt. </p>
     *
     * <p>Diese Art des julianischen Tages repr&auml;sentiert den aktuellen astronomischen Standard.
     * Die Zeit TT-2000-01-01T12:00Z entspricht JD(TT)2451545.0 </p>
     *
     * @param   moment      corresponding moment
     * @return  JulianDay
     * @throws  IllegalArgumentException if the Julian day of moment is not in supported range
     */
    public static JulianDay ofEphemerisTime(Moment moment) {

        return new JulianDay(getValue(moment, TimeScale.TT), TimeScale.TT);

    }

    /**
     * <p>Creates a Julian day on the time scale {@link TimeScale#TT},
     * sometimes also called <em>Julian Ephemeris Day</em>. </p>
     *
     * <p>This kind of Julian day represents the actual astronomical standard. The time
     * TT-2000-01-01T12:00Z corresponds to JD(TT)2451545.0 </p>
     *
     * @param   date        calendar date
     * @param   time        local time
     * @param   offset      timezone offset
     * @return  JulianDay
     * @throws  IllegalArgumentException if the Julian day of moment is not in supported range
     * @since   3.36/4.31
     */
    /*[deutsch]
     * <p>Erzeugt einen julianischen Tag auf der Zeitskala {@link TimeScale#TT},
     * manchmal auch <em>Julian Ephemeris Day</em> genannt. </p>
     *
     * <p>Diese Art des julianischen Tages repr&auml;sentiert den aktuellen astronomischen Standard.
     * Die Zeit TT-2000-01-01T12:00Z entspricht JD(TT)2451545.0 </p>
     *
     * @param   date        calendar date
     * @param   time        local time
     * @param   offset      timezone offset
     * @return  JulianDay
     * @throws  IllegalArgumentException if the Julian day of moment is not in supported range
     * @since   3.36/4.31
     */
    public static JulianDay ofEphemerisTime(
        CalendarDate date,
        PlainTime time,
        ZonalOffset offset
    ) {

        long d = EpochDays.JULIAN_DAY_NUMBER.transform(date.getDaysSinceEpochUTC(), EpochDays.UTC);
        double tod = time.get(PlainTime.NANO_OF_DAY) / (86400.0 * 1000000000L);
        double jde = d - 0.5 + tod - offset.getIntegralAmount() / 86400.0;
        return new JulianDay(jde, TimeScale.TT);

    }

    /**
     * <p>Creates a Julian day on the time scale {@link TimeScale#UT},
     * hence related to the mean solar time. </p>
     *
     * <p>The conversion to the <em>ephemeris time</em> requires a delta-T-correction. </p>
     *
     * @param   value   floating decimal value in range {@code 990575.0 - 2817152.0}
     * @return  JulianDay
     * @throws  IllegalArgumentException if given value is not a finite number or out of range
     */
    /*[deutsch]
     * <p>Erzeugt einen julianischen Tag auf der Zeitskala {@link TimeScale#UT},
     * also bezogen auf die mittlere Sonnenzeit. </p>
     *
     * <p>Die Umrechnung in die <em>ephemeris time</em> erfordert eine delta-T-Korrektur. </p>
     *
     * @param   value   floating decimal value in range {@code 990575.0 - 2817152.0}
     * @return  JulianDay
     * @throws  IllegalArgumentException if given value is not a finite number or out of range
     */
    public static JulianDay ofMeanSolarTime(double value) {

        return new JulianDay(value, TimeScale.UT);

    }

    /**
     * <p>Creates a Julian day on the time scale {@link TimeScale#UT},
     * hence related to the mean solar time. </p>
     *
     * <p>The conversion to the <em>ephemeris time</em> requires a delta-T-correction. </p>
     *
     * @param   moment      corresponding moment
     * @return  JulianDay
     * @throws  IllegalArgumentException if the Julian day of moment is not in supported range
     */
    /*[deutsch]
     * <p>Erzeugt einen julianischen Tag auf der Zeitskala {@link TimeScale#UT},
     * also bezogen auf die mittlere Sonnenzeit. </p>
     *
     * <p>Die Umrechnung in die <em>ephemeris time</em> erfordert eine delta-T-Korrektur. </p>
     *
     * @param   moment      corresponding moment
     * @return  JulianDay
     * @throws  IllegalArgumentException if the Julian day of moment is not in supported range
     */
    public static JulianDay ofMeanSolarTime(Moment moment) {

        return new JulianDay(getValue(moment, TimeScale.UT), TimeScale.UT);

    }

    /**
     * <p>Creates a Julian day on the time scale {@link TimeScale#POSIX}. </p>
     *
     * <p>This conversion does not involve a delta-T-correction. </p>
     *
     * @param   value   floating decimal value in range {@code 990575.0 - 2817152.0}
     * @return  JulianDay
     * @throws  IllegalArgumentException if given value is not a finite number or out of range
     * @since   3.34/4.29
     */
    /*[deutsch]
     * <p>Erzeugt einen julianischen Tag auf der Zeitskala {@link TimeScale#POSIX}. </p>
     *
     * <p>Die Umrechnung erfordert keine delta-T-Korrektur. </p>
     *
     * @param   value   floating decimal value in range {@code 990575.0 - 2817152.0}
     * @return  JulianDay
     * @throws  IllegalArgumentException if given value is not a finite number or out of range
     * @since   3.34/4.29
     */
    public static JulianDay ofSimplifiedTime(double value) {

        return new JulianDay(value, TimeScale.POSIX);

    }

    /**
     * <p>Creates a Julian day on the time scale {@link TimeScale#POSIX}. </p>
     *
     * <p>This conversion does not involve a delta-T-correction. </p>
     *
     * @param   moment      corresponding moment
     * @return  JulianDay
     * @throws  IllegalArgumentException if the Julian day of moment is not in supported range
     * @since   3.34/4.29
     */
    /*[deutsch]
     * <p>Erzeugt einen julianischen Tag auf der Zeitskala {@link TimeScale#POSIX}. </p>
     *
     * <p>Die Umrechnung erfordert keine delta-T-Korrektur. </p>
     *
     * @param   moment      corresponding moment
     * @return  JulianDay
     * @throws  IllegalArgumentException if the Julian day of moment is not in supported range
     * @since   3.34/4.29
     */
    public static JulianDay ofSimplifiedTime(Moment moment) {

        return new JulianDay(getValue(moment, TimeScale.POSIX), TimeScale.POSIX);

    }

    /**
     * <p>Obtains the value of this Julian day starting at noon. </p>
     *
     * @return  double
     */
    /*[deutsch]
     * <p>Liefert den Wert dieses julianischen Tags, der zu Mittag beginnt. </p>
     *
     * @return  double
     */
    public double getValue() {

        return this.value;

    }

    /**
     * <p>Obtains the value of this Julian day as <em>Modified Julian Date</em>
     * starting at midnight. </p>
     *
     * @return  double
     * @see     net.time4j.engine.EpochDays#MODIFIED_JULIAN_DATE
     */
    /*[deutsch]
     * <p>Liefert den Wert dieses julianischen Tags als <em>Modified Julian Date</em>,
     * das zu Mitternacht beginnt. </p>
     *
     * @return  double
     * @see     net.time4j.engine.EpochDays#MODIFIED_JULIAN_DATE
     */
    public double getMJD() {

        return this.value - 2400000.5;

    }

    /**
     * <p>Obtains the value of this Julian day as <em>Julian century relative to the year 2000</em>. </p>
     *
     * @return  Julian century in J2000-frame
     * @since   3.36/4.31
     */
    /*[deutsch]
     * <p>Liefert den Wert dieses julianischen Tags als <em>Julianisches Jahrhundert relativ zum Jahr 2000</em>. </p>
     *
     * @return  Julian century in J2000-frame
     * @since   3.36/4.31
     */
    public double getCenturyJ2000() {

        return (this.value - 2451545.0) / 36525;

    }

    /**
     * <p>Obtains the underlying time scale. </p>
     *
     * @return  TimeScale
     */
    /*[deutsch]
     * <p>Liefert die zugrundeliegende Zeitskala. </p>
     *
     * @return  TimeScale
     */
    public TimeScale getScale() {

        return this.scale;

    }

    /**
     * <p>Adds an amount in decimal days to this Julian day. </p>
     *
     * @param   amount      the amount in decimal days
     * @return  new adjusted instance of Julian day with the same time scale
     */
    /*[deutsch]
     * <p>Addiert einen Betrag in dezimalen Tagen zu diesem julianischen Tag. </p>
     *
     * @param   amount      the amount in decimal days
     * @return  new adjusted instance of Julian day with the same time scale
     */
    public JulianDay plusDays(double amount) {

        return new JulianDay(this.value + amount, this.scale);

    }

    /**
     * <p>Subtracts an amount in decimal days from this Julian day. </p>
     *
     * @param   amount      the amount in decimal days
     * @return  new adjusted instance of Julian day with the same time scale
     */
    /*[deutsch]
     * <p>Subtrahiert einen Betrag in dezimalen Tagen von diesem julianischen Tag. </p>
     *
     * @param   amount      the amount in decimal days
     * @return  new adjusted instance of Julian day with the same time scale
     */
    public JulianDay minusDays(double amount) {

        return new JulianDay(this.value - amount, this.scale);

    }

    /**
     * <p>Adds an amount in decimal seconds to this Julian day. </p>
     *
     * @param   amount      the amount in decimal seconds
     * @return  new adjusted instance of Julian day with the same time scale
     */
    /*[deutsch]
     * <p>Addiert einen Betrag in dezimalen Sekunden zu diesem julianischen Tag. </p>
     *
     * @param   amount      the amount in decimal seconds
     * @return  new adjusted instance of Julian day with the same time scale
     */
    public JulianDay plusSeconds(double amount) {

        return new JulianDay(this.value + amount / DAY_IN_SECONDS, this.scale);

    }

    /**
     * <p>Subtracts an amount in decimal seconds from this Julian day. </p>
     *
     * @param   amount      the amount in decimal seconds
     * @return  new adjusted instance of Julian day with the same time scale
     */
    /*[deutsch]
     * <p>Subtrahiert einen Betrag in dezimalen Sekunden von diesem julianischen Tag. </p>
     *
     * @param   amount      the amount in decimal seconds
     * @return  new adjusted instance of Julian day with the same time scale
     */
    public JulianDay minusSeconds(double amount) {

        return new JulianDay(this.value - amount / DAY_IN_SECONDS, this.scale);

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof JulianDay) {
            JulianDay that = (JulianDay) obj;
            return (this.value == that.value) && (this.scale == that.scale);
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return AstroUtils.hashCode(this.value) ^ this.scale.hashCode();

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("JD(");
        sb.append(this.scale.name());
        sb.append(')');
        sb.append(this.value);
        return sb.toString();

    }

    /**
     * <p>Converts this Julian day to a {@code Moment}. </p>
     *
     * @return  Moment
     */
    /*[deutsch]
     * <p>Wandelt diesen julianischen Tag in einen {@code Moment} um. </p>
     *
     * @return  Moment
     */
    public Moment toMoment() {

        double secs = this.value * DAY_IN_SECONDS;
        TimeScale ts = this.scale;

        if (!LeapSeconds.getInstance().isEnabled() && (ts != TimeScale.POSIX)) {
            if (ts == TimeScale.TT) {
                PlainDate d = PlainDate.of((long) Math.floor(this.getMJD()), EpochDays.MODIFIED_JULIAN_DATE);
                secs -= TimeScale.deltaT(d.getYear(), d.getMonth());
            }
            secs += (DAY_IN_SECONDS * 730);
            ts = TimeScale.POSIX;
        }

        long elapsed = MathUtils.safeSubtract((long) secs, jdOffset(ts));
        int nano = (int) ((secs - Math.floor(secs)) * MRD);

        return Moment.of(elapsed, nano, ts);

    }

    // obtains the JD-value for given moment and scale
    static double getValue(
        Moment moment,
        TimeScale scale
    ) {

        long elapsedTime = moment.getElapsedTime(scale) + jdOffset(scale);
        int nano = moment.getNanosecond(scale);
        double secs = elapsedTime + nano / (MRD * 1.0);
        return secs / DAY_IN_SECONDS;

    }

    private static long jdOffset(TimeScale scale) {

        switch (scale) {
            case UT:
            case TT:
                return OFFSET_1972;
            case POSIX:
                return OFFSET_1970;
            default:
                throw new UnsupportedOperationException(scale.name());
        }

    }

    private static void check(
        double value,
        TimeScale scale
    ) {

        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException("Value is not finite: " + value);
        }

        switch (scale) {
            case UT:
            case TT:
            case POSIX:
                if (Double.compare(MIN, value) > 0 || Double.compare(value, MAX) > 0) {
                    throw new IllegalArgumentException("Out of range: " + value);
                }
                break;
            default:
                throw new IllegalArgumentException("Unsupported time scale: " + scale);
        }

    }

    /**
     * @serialData  Checks the consistency of deserialized data.
     * @param       in      object input stream
     * @throws      StreamCorruptedException if the data are not consistent
     * @throws      IOException if the data cannot be read
     */
    private void readObject(ObjectInputStream in)
        throws IOException {

        try {
            in.defaultReadObject();
            check(this.value, this.scale);
        } catch (ClassNotFoundException ex) {
            throw new StreamCorruptedException();
        } catch (IllegalArgumentException ex) {
            throw new StreamCorruptedException();
        }

    }

}
