function post = create_tables(post,general,parameters)
%     for i=1:length(post.data.postprocess)
%         parameters_data = readtable(general.paths.local_directory_path+string(post.data.sim_files_folder(i))+"_Results/Parameters/parameters.csv");
%         parameters_data.Var2 = cellstr(num2str(parameters_data.Var2));
%         [num_rows, num_columns] = size(parameters_data);
%         
%         for row = 1:num_rows
%             for column = 1:num_columns
%                 parameters_data{row,column} = cellfun(@(x) strrep(x, ',', ''), parameters_data{row,column}, 'UniformOutput', false);
%                 parameters_data{row,column} = cellfun(@(x) strrep(x, ' ', ''), parameters_data{row,column}, 'UniformOutput', false);
%             end
%         end
% 
%         combined_columns = cellstr(strcat(parameters_data{:,2}, {' '}, parameters_data{:,3}));
% 
%         for j = 1:length(combined_columns)
%             post.data{i,((post.dimensions.num_columns)+j)} = cellstr(combined_columns{j});
%         end
    
    parameters_table = [];

    for i = 1:length(post.data.postprocess)
        parameters_data = readcell(general.paths.local_directory_path+string(post.data.sim_files_folder(i))+"_Results/Parameters/parameters.csv","Delimiter", "\t");%         parameters_data.Var2 = cellstr(num2str(parameters_data.Var2));
        parameters_data = cellfun(@(x) strsplit(x, ','), parameters_data, 'UniformOutput', false);
        parameters_data = vertcat(parameters_data{:});

        [num_rows, num_columns] = size(parameters_data);
        
        parameters_table_current = [];
        k=1;

        for row = 2:(num_rows)
            parameter_data_part = table({parameters_data{row,2}}, 'VariableNames', {parameters_data{row,1}});
            parameters_table_current = [parameters_table_current, parameter_data_part];
            
            if mod(k,(num_rows-1)) == 0
                parameters_table = [parameters_table; parameters_table_current];
            end
            k=k+1;
        end
        
        reports_data = readtable(general.paths.local_directory_path+string(post.data.sim_files_folder(i))+"_Results/Reports/reports.csv");
        reports_table(i,:) = reports_data;
    end
    
    post.data = [post.data,parameters_table,reports_table];

            




%         [num_rows, num_columns] = size(post.data);
% 
%         for length(num_)
% 
%         post.data{i,(num_columns+1)} = parameters_data(i,)
% 
%         combined_columns = strcat(parameters_data(:,2), {' '}, parameters_data(:,3));
%         
%         varaible_column = table(parameters_data(:,1), 'VariableNames', {'variable'});


        


