#!/bin/sh
#Created by pengjianqing@gmail.com
#Date: 20131105
#Used to wrapper the ./gradlew tasks, make you happy with just one select.
#When your are the 1st time to run it, it will auto generate the gradlew.tasks.
#So if your tasks are already changed, you need run "./build clean" to remove it and it will be auto generated next time.

gradlew_tasks=gradlew.tasks
task_type="^assemble|^install|^uninstall"

if [ -f $gradlew_tasks ];then
    echo "Already exist $gradlew_tasks"
else
    echo "Generate the $gradlew_tasks"
    ./gradlew tasks > $gradlew_tasks
fi

is_int() {
    if test ${1} -eq ${1} 2>/dev/null; then
        return 0
    fi
    return 1
}

run_command(){
    command=`echo "$tasks"|grep "^${1}"|cut -d "-" -f1|cut -d ":" -f2`
    echo "Run your command: ./gradlew $command"

    if [ -z ${command} ];then
        echo "Can't find your task, please run $0 to check your tasks first."
        exit 0
    else
        ./gradlew $command

        echo "YOU MAYBE NEED THE FOLLOWING COMMANDS"
        name=`echo $command|sed -e "s/install//g" -e "s/uninstall//g" -e "s/Debug//g" -e "s/Test//g"`
        echo "$tasks"|grep ${name}|cut -d "-" -f1
    fi
}

#get the tasks list
tasks=`cat $gradlew_tasks|grep -E "$task_type"|grep -n ""`

if [ ${#} = 1 ];then
    if [ ${1} = 'clean' ];then
        echo ./gradlew clean
        ./gradlew clean
        rm $gradlew_tasks
        exit 0
    elif is_int $1;then
        run_command "${1}:"
    else
        echo  ./gradlew $@
        ./gradlew $@
    fi
else
    echo "$tasks"

    read -p "Please choose your task:" task
    echo "You choose $task"

    run_command "$task:"
fi
