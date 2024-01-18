scp dist/js/*.js springboot:kob/acapp/
scp dist/css/*.css springboot:kob/acapp/

ssh springboot 'cd kob/acapp && ./rename.sh'
