
<?php include('header.php') ?>
<?php include('db.php'); ?>

 <?php 
        $query="SELECT DISTINCT Product FROM RetrieveOpenPR";
        $result=mysqli_query($con,$query);
     ?> 

     <body>
      <form action="search.php" method="POST">
     <select name="option_chosen" style="width: 300px;height: 28px">
        <option><b>-- Select Product --</b></option>
        <?php
            // while(list($lot_number)=mysql_fetch_row($result)) {
            // echo "<option value=\"".$lot_number."\">".$lot_number."</option>";

        	while ($row = mysqli_fetch_array($result)) {
        echo "<option value='" . $row['Product'] . "''>" . $row['Product'] . "</option>";

        }
       ?>
        </select>

     <input type="submit" value="Search..." style="width: 7em;  height: 2em;" />
    </form>


<!--     <script>
    function myFunction() {
    var x = document.getElementById("fr").value;
    document.getElementById("demo").innerHTML = x;
    }
    </script> -->




</body>
</html>