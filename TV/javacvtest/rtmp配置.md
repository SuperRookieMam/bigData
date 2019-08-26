####
安装nginx + nginx-rtmp-module自行搜索安装：


配置nginx虚拟主机

    rtmp{
      server{
        #rtmp 端口  1935为nginx 默认端口
        listen 1935;
    	#流整合的最大的块大小。默认值为 4096。
    	#这个值设置的越大 CPU 负载就越小。这个值不能低于 128。
        chunk_size 4000;
        // 开启回调 推流 拉流,对应的需要回到地址等
        publish_notify on;
        #开始推流回调，url地址为java接口
        #on_publish http://localhost:8089/cloudLive/vin/auth;
        #推流停止回调，url地址为java接口
        #on_done http://localhost:8089/cloudLive/vin/auth;
        #创建一个rtmp 名字为live的应用
        application live {
    	  #直播开启
          live on;
    	  #hls 开启
          hls on;
    	  #hls 的m3u8索引文件存储这个地方需要给到nginx的目录操作权限
          hls_path /home/huiyu/hls/;
    	  #hls 每片长度为5秒
          hls_fragment 5s;
          #边播边存配置
          recorder rec{
    	    #存储所有,包括视频及音频
            record all;
    		#是否添加时间戳到录制文件。
    		#否则的话同样的文件在每一次新的录制发生时将被重写。默认为 off
            record_unique on;
    		#录制文件存储地址配置  这个地方需要给到nginx的目录操作权限
            record_path /home/huiyu/record/;
    		#录制存储文件名字配置
            record_suffix -%Y-%m-%d-%H_%M_%S.flv;
          }
        }
      }
    }

配置直播，hls支持以及状态监控页面

        
         location /hls {
                    types {
                        application/vnd.apple.mpegurl m3u8;
                        #或 application/x-mpegURL
                        video/mp2t ts;
                    }
                    alias /usr/local/nginx/html/hls; #视频流文件目录(自己创建)
                    expires -1;
                    add_header Cache-Control no-cache;
               }
               location /stat {
                    rtmp_stat all;
                    rtmp_stat_stylesheet stat.xsl;
               }
        
               location /stat.xsl {
                   root /usr/local/extend_module/nginx-rtmp-module/;
               }
            }
        }
        
        
 nginx最终配置
 
        worker_processes 1;
        
        events {
            worker_connections 1024;
        }
        
        
        http {
            include mime.types;
            default_type application/octet-stream;
            sendfile on;
            keepalive_timeout 65;
            server {
                listen 80;
                server_name localhost;
                location / {
                    root html;
                    index index.html index.htm;
                }
                error_page 500 502 503 504 /50x.html;
                location = /50x.html {
                    root html;
                }
        
               location /hls {
                    types {
                        application/vnd.apple.mpegurl m3u8;
                        #或 application/x-mpegURL
                        video/mp2t ts;
                    }
                   alias /usr/local/nginx/html/hls; #视频流文件目录(自己创建)
                    expires -1;
                    add_header Cache-Control no-cache;
               }
               location /stat {
                    rtmp_stat all;
                    rtmp_stat_stylesheet stat.xsl;
               }
        
               location /stat.xsl {
                   root /usr/local/extend_module/nginx-rtmp-module/;
               }
        
            }
        }
        rtmp {
            server {
                listen 1935;
                chunk_size 4096;
        
                application hls {
                   live on;
                   hls on;
                   hls_path /usr/local/nginx/html/hls; #视频流文件目录(自己创建)
                   hls_fragment 3s;
                }
            }
        }

        一；直播RTMP模块中一些主要的配置选项
        rtmp
        server  NGINX中的服务块
        listen  监听端口
        application  用于在NGINX配置文件中创建同一个程序块
        timeout  连接过期时间
        ping  测试数据包
        ping_timeout  测试数据包超时时间
        max_streams  最大流数量
        ack_window
        chunk_size
        max_queue
        max_message
        buflen
        out_queue
        out_cork
        
        
        二；在直播时的一些配置项
        live
        meta
        interleave
        wait_key
        wait_video
        publish_notify
        drop_idle_publisher
        sync
        play_restart
        idle_streams
        
        
        三；HLS协议进行m3u8实时直播
        hls
        hls_path
        hls_fragment
        hls_playlist_length
        hls_sync
        hls_continuous
        hls_nested
        hls_base_url
        hls_cleanup
        hls_fragment_naming
        hls_fragment_naming_granularity
        hls_fragment_slicing
        hls_variant
        hls_type
        hls_keys
        hls_key_path
        hls_key_url
        hls_fragments_per_key
        
        
        四；录制直播视频以便回放重播
        record
        record_path
        record_suffix
        record_unique
        record_append
        record_lock
        record_max_size
        record_max_frames
        record_interval
        recorder
        record_notify
        
        
        五；使用HTTP动态自适应不同带宽的视频
        dash
        dash_path
        dash_fragment
        dash_playlist_length
        dash_nested
        dash_cleanup
        MPEG-DASH
        
        
        六；用于视频点播的配置
        play
        play_temp_path
        play_local_path
        
        
        七；拉流转播到其他平台
        pull
        push
        push_reconnect
        session_relay
        
        
        八；直播状态的消息和状态
        on_connect
        on_play
        on_publish
        on_done
        on_play_done
        on_publish_done
        on_record_done
        on_update
        notify_update_timeout
        notify_update_strict
        notify_relay_redirect
        notify_method
        
        
        九；对直播的访问权限控制
        allow
        deny
        Exec 一簇函数（进程往往要调用一种exec函数以执行另一个程序）
        exec_push
        exec_pull
        exec
        exec_options
        exec_static
        exec_kill_signal
        respawn
        respawn_timeout
        exec_publish
        exec_play
        exec_play_done
        exec_publish_done
        exec_record_done
        
        
        十；其他RTMP的配置选项
        access_log  访问日志
        log_format
        max_connections  连接数Limits 限制
        rtmp_stat  数据统计
        rtmp_stat_stylesheet
        Multi-worker live streaming    多线程直播流
        rtmp_auto_push
        rtmp_auto_push_reconnect
        rtmp_socket_dir
        rtmp_control  直播Control 控制模块
        
        
        十一；下面是一个NGINX-RTMP直播配置的范例
        worker_processes  1;
        
        events {
            worker_connections  1024;
        }
        
        #这里开始是牛人技术测试直播的配置信息
        rtmp {
        	server {
        		listen 1935;
        		chunk_size 4096;
        		application hls {
        			live on;
        			hls on;
        			hls_path /byDATA/NginxRtmpNRJS/webroot/tt/hls;
        			hls_fragment 5s;
        		}
        		
        		#用来给115频道
        		application ANuid115 {
        			live on;
        			hls on;
        			hls_path /byDATA/NginxRtmp/webroot/ANuid115;
        			hls_fragment 5s;
        		}
        		
        		
        		#用来测试
        		application ANuid901 {
        			live on;			
        			hls on;					#实时回访
        			wait_key on;				#保护TS切片
        			hls_nested on;				#每个流都自动创建一个文件夹
        			hls_fragment 5s;			#每个ts文件为5s的样子
        			hls_fragment_naming system;#使用系统时间戳命名ts文件
        			hls_playlist_length 10800s;	#保存m3u8列表长度时间，默认是30秒，可考虑三小时10800秒
        			hls_cleanup on;			#是否删除列表中已经没有的媒体块TS文件，默认是开启
        			hls_continuous on;			#连续模式
        			hls_path /byDATA/NginxRtmp/webroot/live/record/ANuid901;	#媒体块ts的位置
        		}
        		
        		application ANuid902 {
        			live on;
        			hls on;
        			hls_path /byDATA/NginxRtmp/webroot/tt/ANuid902;
        			hls_fragment 5s;
        		}
        		
        		application ANuid903 {
        			live on;
        			hls on;
        			hls_path /byDATA/NginxRtmp/webroot/tt/ANuid903;
        			hls_fragment 5s;
        		}
        	}
        }
        
        
        http {
            include       mime.types;
            default_type  application/octet-stream;
            #access_log  logs/access.log  main;
            sendfile        on;
            #tcp_nopush     on;
            #keepalive_timeout  0;
            keepalive_timeout  65;
        
        #byAdd
        #include /byDATA/NginxRtmp/conf/*.conf; 此处可以添加自定义配置文件目录
            #gzip  on;
            server {
                listen       80;
                server_name  localhost;
                #charset koi8-r;
                #access_log  logs/host.access.log  main;
                location / {
        			#byAdd
        			#root   /byDATA/NginxRtmp/webroot/tt;
                    root   html;
                    index  index.html index.htm;
                }
                #error_page  404              /404.html;
                # redirect server error pages to the static page /50x.html
                #
                error_page   500 502 503 504  /50x.html;
                location = /50x.html {
                    root   html;
                }
                # proxy the PHP scripts to Apache listening on 127.0.0.1:80
                #
                #location ~ \.php$ {
                #    proxy_pass   http://127.0.0.1;
                #}
                # pass the PHP scripts to FastCGI server listening on 127.0.0.1:9000
                #
                #location ~ \.php$ {
                #    root           html;
                #    fastcgi_pass   127.0.0.1:9000;
                #    fastcgi_index  index.php;
                #    fastcgi_param  SCRIPT_FILENAME  /scripts$fastcgi_script_name;
                #    include        fastcgi_params;
                #}
                # deny access to .htaccess files, if Apache's document root
                # concurs with nginx's one
                #
                #location ~ /\.ht {
                #    deny  all;
                #}
            }
        	#byAdd
        	server {
                listen       80;
                server_name  flow.320023.com;
        		location /ANuid901 {
        			types {
        				application/vnd.apple.mpegurl m3u8;
        				video/mp2t ts;
        			}
        			root /byDATA/NginxRtmp/webroot/live/record;
        			add_header Cache-Control no-cache;
                }
        		#推流状态查看 http://flow.320023.com/stat
        		location /stat {
        			rtmp_stat all;
        			rtmp_stat_stylesheet stat.xsl;
        		}
        		location /stat.xsl { 
        			root /byDATA/NginxRtmp/Program/nginx-rtmp-module-master/;
        		}
        		
        		#rewrite /ANuid901/(.*).m3u8$ /ANuid901/$1/index.m3u8 last;	#重写用于兼容阿里云m3u8命名格式
        		#rewrite /ANuid901/(.*).ts$ /ANuid901/StreamName/$1.ts last;	#重写让上面m3u8能找到ts文件
            }
            # another virtual host using mix of IP-, name-, and port-based configuration
            #
            #server {
            #    listen       8000;
            #    listen       somename:8080;
            #    server_name  somename  alias  another.alias;
            #    location / {
            #        root   html;
            #        index  index.html index.htm;
            #    }
            #}
            # HTTPS server
            #
            #server {
            #    listen       443 ssl;
            #    server_name  localhost;
            #    ssl_certificate      cert.pem;
            #    ssl_certificate_key  cert.key;
            #    ssl_session_cache    shared:SSL:1m;
            #    ssl_session_timeout  5m;
            #    ssl_ciphers  HIGH:!aNULL:!MD5;
            #    ssl_prefer_server_ciphers  on;
            #    location / {
            #        root   html;
            #        index  index.html index.htm;
            #    }
            #}
        }
