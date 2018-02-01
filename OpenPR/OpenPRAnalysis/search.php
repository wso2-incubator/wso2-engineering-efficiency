

<?php include('header.php'); ?>
<?php include('navbar.php'); ?>
<?php include('db.php'); ?>
 
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Search results</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <link rel="stylesheet" type="text/css" href="style.css"/>
</head>
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
                          <th class="active" colspan="2"><center>No.of open PRs for more than a week</center></th>
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
      


       <div></div>

    <br>

    </div>
    <!--/.row-->
      <div class="row">
        <div class="col-lg-12">
         <div class="col-lg-12" align="right">
            <div ><br><?php include("SearchBar.php") ?><br></div>
        </div>
      </div>
     </div>
<!--/.row-->
<div class="col-lg-12" style="width: 100%" >
            <div class="col-lg-12">
                <div class="panel panel-default">
                    <div class="panel-heading"><i class="fa fa-thumbs-up"> </i><?php $url = htmlspecialchars($_SERVER['HTTP_REFERER']);
              echo "<a href='index.php'> Open pull requests</a>";
            ?></div>
                    <div class="panel-body">
                                <div class="table-responsive">
                        <table class="table table-bordered table-striped table-hover" >
                         <thead style="border-bottom: 3px solid #006400;">
                          <tr>
                          <th class="active">Product</th>
                          <th class="active">Repository URL</th> 
                          <th class="active">Git_Id</th> 
                          <th class="active">Pull Request URL</th> 
                          <th class="active">Hour(s)</th>
                          <th class="active">Day(s)</th>
                          <th class="active">Week(s)</th>
                          <th class="active">Month(s)</th>
                          <th class="active">Years(s)</th>

                          </tr>
                        </thead>
                        <tbody >
         <?php
                      if($_SERVER['REQUEST_METHOD'] =='POST')
                       { $option_chosen=$_POST['option_chosen'];
                       $raw_results="SELECT * FROM RetrieveOpenPR WHERE product='$option_chosen' order by OpenHours desc";
                       $run=mysqli_query($con,$raw_results)or die(mysqli_error($con));;


                        echo '<h3 style="color:#005c99">' . $option_chosen=$_POST['option_chosen'] . '</h3>'; 

         
       
            
             
            while($results=mysqli_fetch_array($run, MYSQLI_ASSOC)){
                echo "<tr>";
                            echo "<td>" . $results['Product'] . "</td>";
                            echo "<td><a href=" . $results['Repourl'] . " target=blank>" . $results['Repourl'] . "</a></td>";
                            echo "<td>" . $results['GitId'] . "</td>";
                            echo "<td><a href=" . $results['PullUrl'] . " target=blank>" . $results['PullUrl'] . "</a></td>";
                            echo "<td>" . number_format($results['OpenHours']) . "</td>";
                            echo "<td>" . number_format($results['OpenDays']) . "</td>";
                            echo "<td>" . number_format($results['OpenWeeks']) . "</td>";
                            echo "<td>" . $results['OpenMonths'] . "</td>";
                            echo "<td>" . $results['OpenYears'] . "</td>";
                            echo "</tr>"; 
            
             
               
                
            }
             
        
       
         
    }
    




?>
                        </tbody>
                          </table>
                    </div>
                    </div>
                </div>
            </div>
        </div><!--/.row-->                      
    </div>
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
<body>

</body>
</html>
