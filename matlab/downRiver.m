%set up passwordless SSH unless you want to be miserable

general = {};

general.paths.template = "onera-m6-sharp_airfoil";

general.paths.local_batch_path = 'C:/Users/younj/Downloads/downRiver/batch.csv';
general.paths.cluster_batch_path = '/storage/coda1/p-sm53/0/jyoun39/project/batch.csv';

general.paths.local_directory_path = 'C:/Users/younj/Downloads/downRiver/matlab/';
general.paths.cluster_directory_path = '/storage/coda1/p-sm53/0/jyoun39/project/';

general.ssh.username = "jyoun39";
general.ssh.hostname = "login-phoenix.pace.gatech.edu";

%CODE FUNCTIONS:
gather = 1; %if gather is 1, runs post.java for cases
collect = 1; %if collect is 1, copies files from cluster to local

post.data = readtable(general.paths.local_batch_path, 'Delimiter', ',');

if gather == 1
    create_post_java(general)
    execute_ps_commands(general,post)
end

%find a method to collect parameters in StarCCM (create a .java file) then
%append into to post.java

%combine all of the reports I want into one table with case name, var1,
%var2, etc.

%read monitors that I want and check last 500 iterations and gather mean
%average value and error

%if collect == 1



