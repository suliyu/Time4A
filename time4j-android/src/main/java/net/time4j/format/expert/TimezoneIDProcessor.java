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

package net.time4j.format.expert;

import net.time4j.engine.AttributeQuery;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoElement;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;

import java.io.IOException;
import java.util.List;
import java.util.Set;


/**
 * <p>Verarbeitet eine Zeitzonen-ID. </p>
 *
 * @author  Meno Hochschild
 * @since   3.0
 */
enum TimezoneIDProcessor
    implements FormatProcessor<TZID> {

    //~ Statische Felder/Initialisierungen --------------------------------

    /** Singleton. */
    INSTANCE;

    //~ Methoden ----------------------------------------------------------

    @Override
    public int print(
        ChronoDisplay formattable,
        Appendable buffer,
        AttributeQuery attributes,
        Set<ElementPosition> positions,
        boolean quickPath
    ) throws IOException {

        if (!formattable.hasTimezone()) {
            throw new IllegalArgumentException(
                "Cannot extract timezone id from: " + formattable);
        }

        int start = -1;
        int printed;

        if (buffer instanceof CharSequence) {
            start = ((CharSequence) buffer).length();
        }

        String canonical = formattable.getTimezone().canonical();
        buffer.append(canonical);
        printed = canonical.length();

        if (
            (start != -1)
            && (printed > 0)
            && (positions != null)
        ) {
            positions.add(
                new ElementPosition(
                    TimezoneElement.TIMEZONE_ID,
                    start,
                    start + printed));
        }

        return printed;

    }

    @Override
    public void parse(
        CharSequence text,
        ParseLog status,
        AttributeQuery attributes,
        ParsedEntity<?> parsedResult,
        boolean quickPath
    ) {

        int len = text.length();
        int start = status.getPosition();
        int pos = start;

        if (pos >= len) {
            status.setError(start, "Missing timezone name.");
            return;
        }

        // Zeitzonen-ID einlesen
        StringBuilder name = new StringBuilder();

        while (pos < len) {
            char c = text.charAt(pos);

            if ( // siehe Theory-Datei in TZDB
                (c >= 'a' && c <= 'z')
                || (c >= 'A' && c <= 'Z')
                || (c == '-')
                || (c == '_')
                || (c == '/')
            ) {
                name.append(c);
                pos++;
            } else {
                break;
            }
        }

        if (!Character.isLetter(name.charAt(name.length() - 1))) {
            name.deleteCharAt(name.length() - 1);
            pos--;
        }

        String key = name.toString();

        // Offset prüfen
        if (key.isEmpty()) {
            status.setError(start, "Missing valid timezone id.");
            return;
        } else if (key.startsWith("Etc/GMT")) {
            status.setError(
                start,
                "Inverse Etc/GMT-Offsets are not supported, "
                + "use UTC-Offsets instead.");
            return;
        } else if (key.equals("Z")) {
            parsedResult.put(TimezoneElement.TIMEZONE_OFFSET, ZonalOffset.UTC);
            status.setPosition(pos);
            return;
        } else if (
            key.equals("UTC")
            || key.equals("GMT")
            || key.equals("UT")
        ) {
            if (len > pos) {
                char c = text.charAt(pos);
                if ((c == '+') || (c == '-')) {
                    status.setPosition(pos);
                    TimezoneOffsetProcessor.EXTENDED_LONG_PARSER.parse(
                        text, status, attributes, parsedResult, quickPath);
                    return;
                }
            }
            parsedResult.put(TimezoneElement.TIMEZONE_OFFSET, ZonalOffset.UTC);
            status.setPosition(pos);
            return;
        }

        // binäre Suche
        List<TZID> zones = Timezone.getAvailableIDs("INCLUDE_ALIAS");
        int low = 0;
        int high = zones.size() - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            TZID zone = zones.get(mid);
            int cmp = zone.canonical().compareTo(key);

            if (cmp < 0) {
                low = mid + 1;
            } else if (cmp > 0) {
                high = mid - 1;
            } else {
                parsedResult.put(TimezoneElement.TIMEZONE_ID, zone);
                status.setPosition(pos);
                return;
            }
        }

        status.setError(start, "Cannot parse to timezone id: " + key);

    }

    @Override
    public ChronoElement<TZID> getElement() {

        return TimezoneElement.TIMEZONE_ID;

    }

    @Override
    public FormatProcessor<TZID> withElement(ChronoElement<TZID> element) {

        return INSTANCE;

    }

    @Override
    public boolean isNumerical() {

        return false;

    }

    @Override
    public FormatProcessor<TZID> quickPath(
        ChronoFormatter<?> formatter,
        AttributeQuery attributes,
        int reserved
    ) {

        return INSTANCE;

    }

}
