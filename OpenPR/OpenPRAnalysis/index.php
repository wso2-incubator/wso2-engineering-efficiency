
<?php include('header.php'); ?>
<?php include('navbar.php'); ?>
<?php include('db.php'); ?>

<HEAD><style type="text/css">
a { text-decoration : none; color : #000; }
</style></HEAD>
<body>
        <div class="col-md-4">
        </div>

        <div class="col-md-4">
        <div class="panel panel-teal panel-widget" style="margin-top: 20px;">
          <div class="row">
            <div class=" col-lg-6 widget-left">
              <img src="request.png" width="100" height="65" /> 
            </div>
            <div class=" widget-right">
              <div class="large"><center><?php
                            $sql = "SELECT COUNT(PullUrl) as ids FROM RetrieveOpenPR where OpenWeeks>1";
                            $result = mysqli_query($con,$sql);
                            
                            $rows = mysqli_fetch_assoc($result);
                                echo $rows['ids']; 
                            
                            ?></center></div>
              <div class="text-muted"><center>Number of open PRs more than a week</center></div>
            </div>
          </div>
        </div>
      </div>

      <div class="col-md-12">
        </div>

      
        <div class="col-sm-3" ><div></div>
        </div>

        <div class="col-sm-6" > 
        <div class="panel panel-default">
          <div class="panel-body">
                <div class="table-responsive">
                        <table class="table table-bordered " >
                         <thead style="border-bottom: 3px solid #ff3300;">
                          <tr>
                          <th class="active" colspan="2"><center>No. of open PRs for more than a week</center></th>
                          </tr>
                        </thead>
                        <tbody >
                       <?php

                         $sql = "SELECT distinct Product,count(PullUrl) FROM RetrieveOpenPR where OpenWeeks>1 group by Product having count(PullUrl) order by count(PullUrl) desc;"; 
                        $query =  mysqli_query($con,$sql) or die(mysqli_error($con));
                        while ($row = mysqli_fetch_array($query)) {
                            echo "<tr>";
                            echo "<td>" . $row['Product'] . "</td>";
                            echo "<td>" . $row['count(PullUrl)'] . "</td>";
                            
 
                            echo "</tr>"; 
                        }

                        ?>
                        </tbody>
                          </table>

          </div>
          </div>
        </div>
        </div>
      
  

      </div>
  

    <br>

    </div><!--/.row-->

      <div class="row">
      <div class="col-lg-12">
        <div class="col-lg-12" align="right">
        <div ><br><?php include("SearchBar.php") ?><br></div>
      </div>
    </div>
  </div>
 


    
     <div class="col-lg-12 " style="width: 100%" >
      <div class="col-lg-12">
        <div class="panel panel-default">
              <div class="panel-heading"><i class="fa fa-thumbs-up">Open pull requests</i></div>
          <div class="panel-body">
                <div class="table-responsive" id="PR_Table">
                        <table class="table table-bordered table-striped table-hover">
                         <thead style="border-bottom: 3px solid #006400;">
                          <tr>
                               <th class="active"><a class="column_sort" id="Product" data-order="desc" href="#">Product</a></th>  
                               <th class="active"><a class="column_sort" id="Repourl" data-order="desc" href="#">Repository URL</a></th>  
                               <th class="active"><a class="column_sort" id="GitId" data-order="desc" href="#">Git_ID</a></th>  
                               <th class="active"><a class="column_sort" id="PullUrl" data-order="desc" href="#">Pull Request URL</a></th>  
                               <th class="active"><a class="column_sort" id="OpenHours" data-order="desc" href="#">Hour(s)</a></th> 
                               <th class="active"><a class="column_sort" id="OpenDays" data-order="desc" href="#">Day(s)</a></th>  
                               <th class="active"><a class="column_sort" id="OpenWeeks" data-order="desc" href="#">Week(s)</a></th>  
                               <th class="active"><a class="column_sort" id="OpenMonths" data-order="desc" href="#">Month(s)</a></th>  
                               <th class="active"><a class="column_sort" id="OpenYears" data-order="desc" href="#">Year(s)</a></th> 

                          </tr>
                        </thead>
                        <tbody >
                       <?php

                         $sql = "select * from RetrieveOpenPR order by OpenHours desc"; 
                        $query =  mysqli_query($con,$sql) or die(mysqli_error($con));
                        while ($row = mysqli_fetch_array($query)) {
                            echo "<tr>";
                            echo "<td>" . $row['Product'] . "</td>";
                            echo "<td><a href=" . $row['Repourl'] ." target=blank>" . $row['Repourl'] .  "</a></td>";
                            echo "<td>" . $row['GitId'] . "</td>";
                            echo "<td><a href=" . $row['PullUrl'] . " target=blank>" . $row['PullUrl'] . "</a></td>";
                            echo "<td>" . number_format($row['OpenHours']) . "</td>";
                            echo "<td>" . number_format($row['OpenDays']) . "</td>";
                            echo "<td>" . number_format($row['OpenWeeks']) . "</td>";
                            echo "<td>" . $row['OpenMonths'] . "</td>";
                            echo "<td>" . $row['OpenYears'] . "</td>";
                            echo "</tr>"; 
                        }

                        ?>
                        </tbody>
                          </table>
          </div>
          </div>
        </div>
      </div>
    </div> <!--/.row            
  </div>  <!--/.main-->
   <script>  
 $(document).ready(function(){  
      $(document).on('click', '.column_sort', function(){  
           var column_name = $(this).attr("id");  
           var order = $(this).data("order");  
           var arrow = '';  
           //glyphicon glyphicon-arrow-up  
           //glyphicon glyphicon-arrow-down  
           if(order == 'desc')  
           {  
                arrow = '&nbsp;<span class="glyphicon glyphicon-arrow-down"></span>';  
           }  
           else  
           {  
                arrow = '&nbsp;<span class="glyphicon glyphicon-arrow-up"></span>';  
           }  
           $.ajax({  
                url:"sort.php",  
                method:"POST",  
                data:{column_name:column_name, order:order},  
                success:function(data)  
                {  
                     $('#PR_Table').html(data);  
                     $('#'+column_name+'').append(arrow);  
                }  
           })  
      });  
 });  
 </script>

</body>
