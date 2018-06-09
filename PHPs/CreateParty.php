<?php
//Require password.php enables the function password_hash() to be called
    require("password.php");
//Establishes connection with database
    $connect = mysqli_connect("db.soic.indiana.edu", "i494f17_team51", "my+sql=i494f17_team51", "i494f17_team51");
    date_default_timezone_set('EST');
//pulls ALREADY VALIDATED data from Android Studios, Sanatizes it, and assignes it to various $variables
    $partyTitle = mysqli_real_escape_string($connect, $_POST["partyTitle"]);
    $partyPassword = mysqli_real_escape_string($connect, $_POST["partyPassword"]);
	$partyDemographics = mysqli_real_escape_string($connect, $_POST["partyDemographics"]);
	$partyRadius = mysqli_real_escape_string($connect, $_POST["partyRadius"]);
	$partyTimeframe = mysqli_real_escape_string($connect, $_POST["partyTimeframe"]);
    $username = mysqli_real_escape_string($connect, $_POST["username"]);
    $latitude = mysqli_real_escape_string($connect, $_POST["latitude"]);
    $longitude = mysqli_real_escape_string($connect, $_POST["longitude"]);


	// $partyTitle = "marjdin";
	// $partyPassword = "password";
	// $partyDemographics = "18+";
	// $partyRadius = "25ft";
	// $partyTimeframe = "NO";
 //    $username = "bennett";
 //    $latitude = 39.169097;
 //    $longitude = -86.527290;

//Create an array of responses to send back to android via 'echo json_encode()', initially false
    $response = array();
    $response["availableTitle"] = false;
    $response["success"] = false;
	
    if (titleAvailable()){
        $response["availableTitle"] = true;
        registerParty();
        $response["success"] = true;
    }

    InitializePartyTable();
    echo json_encode($response);
    


    function registerParty() {
        global $connect, $partyTitle, $partyPassword, $partyDemographics, $partyRadius, $partyTimeframe, $longitude, $latitude;
        $passwordHash = password_hash($partyPassword, PASSWORD_DEFAULT);
        $statement = mysqli_prepare($connect, "INSERT INTO Parties (title, password, demographic, radius, timeframe, latitude, longitude) VALUES (?, ?, ?, ?, ?, ?, ?)");
        mysqli_stmt_bind_param($statement, "sssssss", $partyTitle, $passwordHash, $partyDemographics, $partyRadius, $partyTimeframe, $latitude, $longitude);
        mysqli_stmt_execute($statement);
        mysqli_stmt_close($statement);     
    }

    function titleAvailable(){
        global $connect, $partyTitle;
        $statement = mysqli_prepare($connect, "SELECT * FROM Parties WHERE title = ? AND active='1'"); 
        mysqli_stmt_bind_param($statement, "s", $partyTitle);
        mysqli_stmt_execute($statement);
        mysqli_stmt_store_result($statement);
        $count = mysqli_stmt_num_rows($statement);
        mysqli_stmt_close($statement); 
        if ($count < 1){
            return true; 
        }else {
            return false; 
        }
    }

    function InitializePartyTable(){
        global $connect, $partyTitle;
            //$sql = "Create Table " . $partyTitle . " (id int(4) AUTO_INCREMENT NOT NULL, title varchar(225) not null, link varchar(255) NOT NULL, likes int(3) not null DEFAULT 0, dislikes int(3) not null DEFAULT 0 , played varchar(1) DEFAULT '0', primary key(id))";

            //Create Table testCornhole (id int(4) AUTO_INCREMENT NOT NULL, title varchar(225) not null, link varchar(255) NOT NULL, cornhole int(3) DEFAULT 0, likes int(3) not null DEFAULT 0, dislikes int(3) not null DEFAULT 0 , played varchar(1) DEFAULT '0', primary key(id))

            //CORNHOLE COLUMN ADDED
            $sql = "CREATE TABLE " . $partyTitle . " (id int(4) AUTO_INCREMENT NOT NULL, title varchar(225) not null, link varchar(255) NOT NULL, cornhole int(3) DEFAULT 0, likes int(3) not null DEFAULT 0, dislikes int(3) not null DEFAULT 0 , played varchar(1) DEFAULT '0', thumbnailURL varchar(250) DEFAULT 'ClutchTunes', primary key(id))";

            mysqli_query($connect, $sql); 
            

    }









?>