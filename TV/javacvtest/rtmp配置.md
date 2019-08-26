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
