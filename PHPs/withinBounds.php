<?php 
/* PARTY TERMINATED, set Parties active column equal to the string "0" 
*/
//Establishes connection with database
    $connect = mysqli_connect("db.soic.indiana.edu", "i494f17_team51", "my+sql=i494f17_team51", "i494f17_team51") or die ('Unable to connect');

    if(mysqli_connect_error($connect))
    {
        echo "failed to connect";
    }


    //$username = mysqli_real_escape_string($connect, $_POST["username"]);
    //$userLat = mysqli_real_escape_string($connect, $_POST["latitude"]);
    //$userLong = mysqli_real_escape_string($connect, $_POST["longitude"]);
    //$partyTitle = mysqli_real_escape_string($connect, $_POST["partyTitle"]);
	$username = "test";
	$userLat=39.172958;
	$userLong=-86.523109;
	$partyTitle = "testParty2";
	

    function withinBounds(){
	global $connect, $partyTitle, $userLong, $userLat;
	$statement = mysqli_prepare($connect, "SELECT radius, latitude, longitude FROM Parties WHERE active = '1' AND title = ?");
	mysqli_stmt_bind_param($statement, "s", $partyTitle);
	mysqli_stmt_execute($statement);
	mysqli_stmt_store_result($statement);
	mysqli_stmt_bind_result($statement, $colRadius, $colLatitude, $colLongitude);
		
	if ($colRadius == '25ft'){
            $partyRadius = 5;
        } 
        elseif ($colRadius == '50ft'){
            $partyRadius = 50;
        }elseif ($colRadius == '100ft'){
            $partyRadius = 100;
        }else{
            $partyRadius = 200;
        }
	if (mysqli_stmt_fetch($statement)){
		if(inRange($userLat, $userLong, $colLatitude, $colLongitude, $partyRadius) == "yes"){
			print true;
			return true;
		}
		else{
			print false;
			return false;
		}
	}
	
	}


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
            return true;
        }else{
            return false;
        }
    }
	
	$response = array();
	$response["success"] = false;
	
	if (withinBounds()){
		$response["success"] = true;
	}
	
	echo json_encode($response);

?>
	
	

