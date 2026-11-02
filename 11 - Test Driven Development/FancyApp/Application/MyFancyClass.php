<?php
/**
 * Created by PhpStorm.
 * User: s.kemper
 * Date: 29.03.2022
 * Time: 22:05
 *
 * This class does a lot of things... but has no productive purpose.
 * The only purpose of this class is to teach the basic functions
 * of PHP and how to use them.
 */

class MyFancyClass
{
    /**
     * Kuerzt $text auf maximal $length Zeichen (inkl. $ending).
     * Gibt false zurueck, wenn $ending allein schon laenger als $length ist.
     */
    public function shortString($text, $length, $ending = '...') {
        // mb_*-Funktionen, damit Umlaute als ein Zeichen zaehlen
        if (mb_strlen($ending) > $length) {
            return false;
        }

        if (mb_strlen($text) <= $length) {
            return $text;
        }

        return mb_substr($text, 0, $length - mb_strlen($ending)) . $ending;
    }

    /**
     * Berechnet den Durchschnitt von $values (Zahlen oder numerische Strings).
     * Leeres Array ergibt 0, ein nicht-numerischer Wert ergibt false.
     */
    public function calcAverage($values) {
        if (count($values) === 0) {
            return 0;
        }

        foreach ($values as $value) {
            if (!is_numeric($value)) {
                return false;
            }
        }

        return array_sum($values) / count($values);
    }

    /**
     * Wandelt ein Array in einen String um (implode) und einen String in ein
     * Array (explode). Alle anderen Typen ergeben false.
     */
    public function getOpposite($value, $delimiter = ',') {
        if (is_array($value)) {
            return implode($delimiter, $value);
        }

        // explode wirft ab PHP 8 bei leerem Trennzeichen einen ValueError
        if (is_string($value) && $delimiter !== '') {
            return explode($delimiter, $value);
        }

        return false;
    }
}