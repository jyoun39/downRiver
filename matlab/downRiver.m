%set up passwordless SSH unless you want to be miserable

general = {};

general.paths.template = "onera-m6-sharp_airfoil";

general.paths.local_batch_path = 'C:/Users/younj/Documents/GitHub/downRiver/batch.csv';
general.paths.cluster_batch_path = '/storage/coda1/p-sm53/0/jyoun39/project/batch.csv';

general.paths.local_directory_path = 'C:/Users/younj/Documents/GitHub/downRiver/matlab/';
general.paths.cluster_directory_path = '/storage/coda1/p-sm53/0/jyoun39/project/';

general.ssh.username = "jyoun39";
general.ssh.hostname = "login-phoenix.pace.gatech.edu";
general.ssh.local_processers = "7";

%CODE FUNCTIONS:
gather = 0; %if gather is 1, runs post.java for cases
collect = 1; %if collect is 1, copies files from cluster to local

post.data = readtable(general.paths.local_batch_path, 'Delimiter', ',');

% Initialize a logical index array to keep track of rows to delete
rowsToDelete = false(height(post.data), 1);

% Loop through the rows to identify which ones to delete
for i = 1:height(post.data)
    if post.data.postprocess(i) == 0
        rowsToDelete(i) = true;
    end
end

% Delete the identified rows
post.data(rowsToDelete, :) = [];

post.dimensions.num_columns = width(post.data);
post.dimensions.num_rows = height(post.data);

parameters = {'alpha', 'new1', 'Lref'};

if gather == 1
    create_post_java(general, parameters)
    execute_ps_commands(general,post)
end

 if collect == 1
     post = create_tables(post,general,parameters);%append reports to post.data (next: add parameters ~in progress)
     %post = read_monitors(post,general);
 end


