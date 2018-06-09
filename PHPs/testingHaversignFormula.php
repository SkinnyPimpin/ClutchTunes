<?php

	$closeEnough = false; //global variable

    function inRange( $userLat, $userLong, $hostLat, $hostLong, $partyRadius) { 
        global $closeEnough; // bring global into function
        $radius = 20900000;  //Earth's Radius in feet: 20.9 milli

        //https://stackoverflow.com/questions/12439801/how-to-check-if-a-certain-coordinates-fall-to-another-coordinates-radius-using-p
        $dLat = deg2rad( $hostLat - $userLat );  
        $dLon = deg2rad( $hostLong - $userLong );  
        //https://www.movable-type.co.uk/scripts/latlong.html
        $squareOfHalfChordLength = sin($dLat/2) * sin($dLat/2) + cos(deg2rad($userLat)) * cos(deg2rad($hostLat)) * sin($dLon/2) * sin($dLon/2);  
        $angularDistanceInRadians = 2 * asin(sqrt($squareOfHalfChordLength));  
        $distance = $radius * $angularDistanceInRadians;  

        //return $d;
        if ($partyRadius > $distance){
            $closeEnough = true;
        }
        echo $distance. "    ";
        return $closeEnough;
    }

    //Play around with points and radius to see how it works
 	echo inRange(39.169097, -86.527290,  39.172897, -86.523155, 25); 

?>


