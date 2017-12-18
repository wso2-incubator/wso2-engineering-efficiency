 <?php  
 include('db.php'); 
 $output = '';  
 $order = $_POST["order"];  
 if($order == "desc")  
 {  
      $order = "asc";  
 }  
 else  
 {  
      $order = "desc";  
 }  
 $query = "SELECT * FROM RetrieveOpenPR ORDER BY ".$_POST["column_name"]." ".$_POST["order"]."";  
 $result = mysqli_query($con, $query) or die(mysqli_error($con));  
 $output .= '  
 <table class="table table-bordered table-striped table-hover">  
      <tr>  
                               <th class="active"><a class="column_sort" id="Product" data-order="'.$order.'" href="#">Product</a></th> 
                               <th class="active"><a class="column_sort" id="Repourl" data-order="'.$order.'" href="#">Repository URL</a></th>  
                               <th class="active"><a class="column_sort" id="GitId" data-order="'.$order.'" href="#">Git_ID</a></th>  
                               <th class="active"><a class="column_sort" id="PullUrl" data-order="'.$order.'" href="#">Pull Request URL</a></th>  
                               <th class="active"><a class="column_sort" id="OpenHours" data-order="'.$order.'" href="#">Hour(s)</a></th> 
                               <th class="active"><a class="column_sort" id="OpenDays" data-order="'.$order.'" href="#">Day(s)</a></th>  
                               <th class="active"><a class="column_sort" id="OpenWeeks" data-order="'.$order.'" href="#">Week(s)</a></th>  
                               <th class="active"><a class="column_sort" id="OpenMonths" data-order="'.$order.'" href="#">Month(s)</a></th>  
                               <th class="active"><a class="column_sort" id="OpenYears" data-order="'.$order.'" href="#">Year(s)</a></th> 
      </tr>  
 ';  
 while($row = mysqli_fetch_array($result))  
 {  
      $output .= '  
      <tr>  
           <td>' . $row["Product"] . '</td>    
           <td><a href=' . $row['Repourl'] .' target=blank>' . $row['Repourl'] .  '</a></td> 
           <td>' . $row["GitId"] . '</td>  
           <td><a href=' . $row['PullUrl'] .' target=blank>' . $row['PullUrl'] .  '</a></td>
           <td>' . number_format($row["OpenHours"]) . '</td> 
           <td>' . number_format($row["OpenDays"]) . '</td> 
           <td>' . number_format($row["OpenWeeks"]) . '</td> 
           <td>' . $row["OpenMonths"] . '</td> 
           <td>' . $row["OpenYears"] . '</td>   

      </tr>  
      ';  
 }  
 $output .= '</table>';  
 echo $output;  
 ?> 