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

    public function calcAverage($values) {
    }

    public function getOpposite($value, $delimiter = ',') {
    }
}