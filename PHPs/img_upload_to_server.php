<?php
// Create connection
//Establishes connection with database
$conn = mysqli_connect("db.soic.indiana.edu", "i494f17_team51", "my+sql=i494f17_team51", "i494f17_team51");

 if($_SERVER['REQUEST_METHOD'] == 'POST')
 {
	 $DefaultId = 0;
	 
	 $ImageData = $_POST['image_path'];
	 
	 $ImageName = $_POST['image_name'];

	 $GetOldIdSQL ="SELECT image_name FROM UploadImageToServer ORDER BY id ASC";
	 
	 $Query = mysqli_query($conn,$GetOldIdSQL);
	 
	 while($row = mysqli_fetch_array($Query)){
	 
	 	$DefaultId = $row['image_name'];
	 }
	 
	 $ImagePath = "images/$DefaultId.png";
	 
	 $ServerURL = "https://burrow.soic.indiana.edu/~team51/$ImagePath";
	 
	 $InsertSQL = "insert into UploadImageToServer (image_path,image_name) values ('$ServerURL','$ImageName')";
	 
	 if(mysqli_query($conn, $InsertSQL)){

	 file_put_contents($ImagePath,base64_decode($ImageData));

	 echo "Your Image Has Been Uploaded.";
	 }
	 
	 mysqli_close($conn);
 }
 else{
 echo "Not Uploaded";
 }

?>