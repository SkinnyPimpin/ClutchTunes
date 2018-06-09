<?php 
/* PARTY TERMINATED, set Parties active column equal to the string "0" 
*/
//Establishes connection with database
    $connect = mysqli_connect("db.soic.indiana.edu", "i494f17_team51", "my+sql=i494f17_team51", "i494f17_team51") or die ('Unable to connect');
    if(mysqli_connect_error($connect))
    {
        echo "failed to connect";
    }
    date_default_timezone_set('EST');
    $username = mysqli_real_escape_string($connect, $_POST["username"]);
    $userLat = mysqli_real_escape_string($connect, $_POST["latitude"]);
    $userLong = mysqli_real_escape_string($connect, $_POST["longitude"]);
    // $username = "bennett";
    // $userLat = 39.169097;
    // $userLong = -86.527290;
    $statement = mysqli_prepare($connect, "SELECT * FROM Users WHERE username = ?");
    mysqli_stmt_bind_param($statement, "s", $username);
    mysqli_stmt_execute($statement);
    mysqli_stmt_store_result($statement);
    mysqli_stmt_bind_result($statement, $colUserID, $colUsername, $colPassword, $colFN, $colEmail, $colHash, $colActive, $colDOB, $colBiography);
    while(mysqli_stmt_fetch($statement)){
        $month = substr($colDOB, 0, -7);
        $day = substr($colDOB, 3, -5);
        $year = substr($colDOB, 6);
        
        $stampBirth = mktime(0, 0, 0, $month, $day, $year);

        $today['day'] = date('d');
        $today['month'] = date('m');
        $today['year'] = date('y') - 18;
        $stampToday = mktime(0, 0, 0, $today['month'], $today['day'], $today['year']);

        if ($stampBirth < $stampToday) {
            populateAllParties();
        } else {
            populateUnderageParties();
       }
    }
    
    function populateAllParties(){
        global $connect, $userLong, $userLat, $active, $demographic, $partyRadius;
        
        $statement = mysqli_prepare($connect, "SELECT * FROM Parties WHERE active = '1'");
        mysqli_stmt_execute($statement);
        mysqli_stmt_store_result($statement);
        mysqli_stmt_bind_result($statement, $colID, $colTitle, $colPassword, $colDemographic, $colRadius, $colTimeframe, $colActive, $colAttending, $colLatitude, $colLongitude);
        if ($colRadius = '25ft'){
            $partyRadius = 25;
        } 
        elseif ($colRadius = '50ft'){
            $partyRadius = 50;
        }elseif ($colRadius = '100ft'){
            $partyRadius = 100;
        }else{
            $partyRadius = 200;
        }
        $flag = array();
        while(mysqli_stmt_fetch($statement)){
            if(inRange($userLat, $userLong, $colLatitude, $colLongitude, $partyRadius)){
                //echo "| ".$colTitle."|";
                $flag[] = $colTitle;
            }            
        }
        print(json_encode($flag));
    }
    function populateUnderageParties(){
        global $connect, $userLong, $userLat, $active, $demographic, $partyRadius;
        
        $statement = mysqli_prepare($connect, "SELECT * FROM Parties WHERE active = '1' AND demographic = 'Not 18'");
        mysqli_stmt_execute($statement);
        mysqli_stmt_store_result($statement);
        mysqli_stmt_bind_result($statement, $colID, $colTitle, $colPassword, $colDemographic, $colRadius, $colTimeframe, $colActive, $colAttending, $colLatitude, $colLongitude);
        if ($colRadius = '25ft'){
            $partyRadius = 25;
        } 
        elseif ($colRadius = '50ft'){
            $partyRadius = 50;
        }elseif ($colRadius = '100ft'){
            $partyRadius = 100;
        }else{
            $partyRadius = 200;
        }
        $flag = array();
        while(mysqli_stmt_fetch($statement)){
            if(inRange($userLat, $userLong, $colLatitude, $colLongitude, $partyRadius)){
                //echo "| ".$colTitle."|";
                $flag[] = $colTitle;
            }            
        }
        print(json_encode($flag));
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
?>