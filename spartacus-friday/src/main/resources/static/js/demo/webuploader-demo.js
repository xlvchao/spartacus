jQuery(function() {
    var $ = jQuery,    // just in case. Make sure it's not an other libaray.

    $wrap = $('#uploader'),

    // 图片容器
    $queue = $('<ul class="filelist"></ul>').appendTo( $wrap.find('.queueList') ),

    // 状态栏，包括进度和控制按钮
    $statusBar = $wrap.find('.statusBar'),

    // 文件总体选择信息。
    $info = $statusBar.find('.info'),

    // 上传按钮
    $upload = $wrap.find('.uploadBtn'),

    // 没选择文件之前的内容。
    $placeHolder = $wrap.find('.placeholder'),

    // 总体进度条
    $progress = $statusBar.find('.progress').hide(),

    // 添加的文件数量
    fileCount = 0,

    // 添加的文件总大小
    fileSize = 0,

    // 优化retina, 在retina下这个值是2
    ratio = window.devicePixelRatio || 1,

    // 缩略图大小
    thumbnailWidth = 110 * ratio,
    thumbnailHeight = 110 * ratio,

    // 可能有pedding, ready, uploading, confirm, done.
    state = 'pedding',

    // 所有文件的进度信息，key为file id
    percentages = {},

    supportTransition = (function(){
        var s = document.createElement('p').style,
            r = 'transition' in s ||
                  'WebkitTransition' in s ||
                  'MozTransition' in s ||
                  'msTransition' in s ||
                  'OTransition' in s;
        s = null;
        return r;
    })(),

    // WebUploader实例
    uploader;
    
    // 记录上传的每个文件名称
	var _global_file = null;
	var _global_file_name = null;

    if ( !WebUploader.Uploader.support() ) {
        parent.layer.msg('Web Uploader 不支持您的浏览器！', {
		    icon: 2,
			time:1000
		});
        throw new Error( 'WebUploader does not support the browser you are using.' );
    }


    var base_domain = getConfig('base_domain');

    // 实例化
    uploader = WebUploader.create({
        pick: {
            id: '#filePicker',
            label: '点击选择文件'
        },
        dnd: '#uploader .queueList',
        paste: document.body,
		
		// 选完文件后，是否自动上传
    	auto: false,
    
		//webuploader默认压缩图片,设置compress:false，可以按照原始比例上传图片
		compress: false,
		
        accept: {
            title: 'Files',
            extensions: 'gif,jpg,jpeg,bmp,png,mp3,aac,wav,mp4,mpeg,avi,wmv,flv,rmvb,doc,docx,xls,xlsx,ppt,pptx,pdf,zip,rar,7z,tar',
            mimeTypes: 'image/*,audio/*,video/*,application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document,application/vnd.ms-excel,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,application/vnd.ms-powerpoint,application/vnd.openxmlformats-officedocument.presentationml.presentation,application/pdf,application/zip,application/x-rar-compressed,application/x-7z-compressed,application/x-tar'
        },

        disableGlobalDnd: true,
		
		// flash版本的WU才需要这个文件
//		swf: 'js/plugins/webuploader/Uploader.swf',
		
        chunked: false, //关闭分片上传
        server: base_domain+'/spartacus-resource/cos/webUploader',
        /*fileNumLimit: 100,
        fileSizeLimit: 500 * 1024 * 1024,    // 500 M
        fileSingleSizeLimit: 5 * 1024 * 1024,    // 5 M*/
        fileNumLimit: parent.fileNumLimit,
        fileSizeLimit: parent.fileSizeLimit * 1024 * 1024,
        fileSingleSizeLimit: parent.fileSingleSizeLimit * 1024 * 1024,
        
        threads: 10	//上传并发数。允许同时最大上传进程数,为了保证文件上传顺序
    });

    // 添加按钮
    uploader.addButton({
        id: '#filePicker2',
        label: '继续添加'
    });
    
    // 当有文件添加进来时执行，负责view的创建
    function addFile( file ) {
        var $li = $( '<li id="' + file.id + '">' +
                '<p class="title">' + file.name + '</p>' +
                '<p class="imgWrap"></p>'+
                '<p class="progress"><span></span></p>' +
                '</li>' ),

            $btns = $('<div class="file-panel">' +
                '<span class="cancel">删除</span>' +
                '<span class="rotateRight">向右旋转</span>' +
                '<span class="rotateLeft">向左旋转</span></div>').appendTo( $li ),
            $prgress = $li.find('p.progress span'),
            $wrap = $li.find( 'p.imgWrap' ),
            $info = $('<p class="error"></p>'),

            showError = function( code ) {
                switch( code ) {
                    case 'exceed_size':
                        text = '文件大小超出';
                        break;

                    case 'interrupt':
                        text = '上传暂停';
                        break;

                    default:
                        text = '上传失败，请重试';
                        break;
                }

                $info.text( text ).appendTo( $li );
            };

        if ( file.getStatus() === 'invalid' ) {
            showError( file.statusText );
        } else {
            // @todo lazyload
            $wrap.text( '预览中' );
            uploader.makeThumb( file, function( error, src ) {
                if ( error ) {
                    $wrap.text( '无法预览' );
                    return;
                }

                var img = $('<img src="'+src+'">');
                $wrap.empty().append( img );
            }, thumbnailWidth, thumbnailHeight );

            percentages[ file.id ] = [ file.size, 0 ];
            file.rotation = 0;
        }
		
        file.on('statuschange', function( cur, prev ) {
            if ( prev === 'progress' ) {
                $prgress.hide().width(0);
            } else if ( prev === 'queued' ) {
                $li.off( 'mouseenter mouseleave' );
                $btns.remove();
            }

            // 成功
            if ( cur === 'error' || cur === 'invalid' ) {
                showError( file.statusText );
                percentages[ file.id ][ 1 ] = 1;
            } else if ( cur === 'interrupt' ) {
                showError( 'interrupt' );
            } else if ( cur === 'queued' ) {
                percentages[ file.id ][ 1 ] = 0;
            } else if ( cur === 'progress' ) {
                $info.remove();
                $prgress.css('display', 'block');
            } else if ( cur === 'complete' ) {
                $li.append( '<span class="success"></span>' );
            }

            $li.removeClass( 'state-' + prev ).addClass( 'state-' + cur );
        });

        $li.on( 'mouseenter', function() {
            $btns.stop().animate({height: 30});
        });

        $li.on( 'mouseleave', function() {
            $btns.stop().animate({height: 0});
        });

        $btns.on( 'click', 'span', function() {
            var index = $(this).index(),
                deg;

            switch ( index ) {
                case 0:
                    uploader.removeFile( file );
                    return;

                case 1:
                    file.rotation += 90;
                    break;

                case 2:
                    file.rotation -= 90;
                    break;
            }

            if ( supportTransition ) {
                deg = 'rotate(' + file.rotation + 'deg)';
                $wrap.css({
                    '-webkit-transform': deg,
                    '-mos-transform': deg,
                    '-o-transform': deg,
                    'transform': deg
                });
            } else {
                $wrap.css( 'filter', 'progid:DXImageTransform.Microsoft.BasicImage(rotation='+ (~~((file.rotation/90)%4 + 4)%4) +')');
            }

        });

        $li.appendTo( $queue );
    }

    // 负责view的销毁
    function removeFile( file ) {
        var $li = $('#'+file.id);

        delete percentages[ file.id ];
        updateTotalProgress();
        $li.off().find('.file-panel').off().end().remove();
    }

    function updateTotalProgress() {
        var loaded = 0,
            total = 0,
            spans = $progress.children(),
            percent;

        $.each( percentages, function( k, v ) {
            total += v[ 0 ];
            loaded += v[ 0 ] * v[ 1 ];
        } );

        percent = total ? loaded / total : 0;

        spans.eq( 0 ).text( Math.round( percent * 100 ) + '%' );
        spans.eq( 1 ).css( 'width', Math.round( percent * 100 ) + '%' );
        updateStatus();
    }

    function updateStatus() {
        var text = '', stats;

        if ( state === 'ready' ) {
            text = '选中' + fileCount + '个文件，共' +
                    WebUploader.formatSize( fileSize ) + '。';
        } else if ( state === 'confirm' ) {
            stats = uploader.getStats();
            if ( stats.uploadFailNum ) {
                text = '已成功上传' + stats.successNum+ '个文件，'+
                    stats.uploadFailNum + '个文件上传失败，<a class="retry" href="#">重新上传</a>失败文件'
            }
        } else {
            stats = uploader.getStats();
            text = '共' + fileCount + '个（' +
                    WebUploader.formatSize( fileSize )  +
                    '），已上传' + stats.successNum + '个';

            if ( stats.uploadFailNum ) {
                text += '，上传失败' + stats.uploadFailNum + '个';
            }
        }

        $info.html( text );
    }

    function setState( val ) {
        var file, stats;

        if ( val === state ) {
            return;
        }

        $upload.removeClass( 'state-' + state );
        $upload.addClass( 'state-' + val );
        state = val;

        switch ( state ) {
            case 'pedding':
                $placeHolder.removeClass( 'element-invisible' );
                $queue.parent().removeClass('filled');
                $queue.hide();
                $statusBar.addClass( 'element-invisible' );
                uploader.refresh();
                break;

            case 'ready':
                $placeHolder.addClass( 'element-invisible' );
                $( '#filePicker2' ).removeClass( 'element-invisible');
                $queue.parent().addClass('filled');
                $queue.show();
                $statusBar.removeClass('element-invisible');
                uploader.refresh();
                break;

            case 'uploading':
                $( '#filePicker2' ).addClass( 'element-invisible' );
                $progress.show();
                $upload.text( '暂停上传' );
                break;

            case 'paused':
                $progress.show();
                $upload.text( '继续上传' );
                break;

            case 'confirm':
                $progress.hide();
                $upload.text( '开始上传' ).addClass( 'disabled' );

                stats = uploader.getStats();
                if ( stats.successNum && !stats.uploadFailNum ) {
                    setState( 'finish' );
                    return;
                }
                break;
            case 'finish':
                stats = uploader.getStats();
                if ( stats.successNum && !stats.uploadFailNum) {
                    parent.layer.msg('所有文件均已上传成功！', {
					    icon: 1,
						time:1000
					});
                } else {
                    // 没有成功的图片，重设
                    parent.layer.msg('所有文件均上传失败，即将重置！', {
					    icon: 2,
						time:1000
					});
                    state = 'done';
                    location.reload();
                }
                break;
        }

        updateStatus();
    }

    uploader.on('uploadBeforeSend', function( object, data, headers ) {
        var jwt = JSON.parse(window.localStorage.getItem('spartacus-jwt') || '{}');
        headers.Authorization = "bearer " + jwt.access_token;
        headers.clientId = "spartacus-friday";
    });

    uploader.on('uploadProgress', function( file, percentage ) {
        var $li = $('#'+file.id),
            $percent = $li.find('.progress span');

        $percent.css( 'width', percentage * 100 + '%' );
        percentages[ file.id ][ 1 ] = percentage;
        updateTotalProgress();
    });

	uploader.on( 'beforeFileQueued', function( file ) {
        _global_file = file;
        _global_file_name = file.name;
    });
    
            
    uploader.on('fileQueued', function( file ) {
        fileCount++;
        fileSize += file.size;

        if ( fileCount === 1 ) {
            $placeHolder.addClass( 'element-invisible' );
            $statusBar.show();
        }

        addFile( file );
        setState( 'ready' );
        updateTotalProgress();
    });

    uploader.on('fileDequeued', function( file ) {
        fileCount--;
        fileSize -= file.size;

        if ( !fileCount ) {
            setState( 'pedding' );
        }

        removeFile( file );
        updateTotalProgress();
    });

	// 当选择文件后validate不通过时，会以派送错误事件的形式通知调用者
    uploader.on('error', function (code) {
    	var str="";
		switch(code) {
			case "F_DUPLICATE":
				str = _global_file_name + " 文件重复！";
				break;
			case "Q_TYPE_DENIED":
				str = _global_file_name + " 不允许的文件类型！";
				break;
			case "F_EXCEED_SIZE":
				str = _global_file_name + ' 文件大小超出限制！单文件大小限制为'+parent.fileSingleSizeLimit+'MB';
				break;
			case "Q_EXCEED_SIZE_LIMIT":
				str = '文件总大小超出限制！总文件大小限制为'+parent.fileSingleSizeLimit+'MB';

				break;
			case "Q_EXCEED_NUM_LIMIT":
				str = "文件总数量超出限制！文件总数量限制为"+parent.fileNumLimit+"个";
		
			default:
				str = "未知错误，错误码:" + code;
		}
		parent.layer.alert(str, {icon: 2});
     });

	uploader.on( 'all', function( type ) {
        switch( type ) {
            case 'uploadFinished':
                setState( 'confirm' );
                break;

            case 'startUpload':
                setState( 'uploading' );
                break;

            case 'stopUpload':
                setState( 'paused' );
                break;
        }
    });
    
    //uploadAccept返回true才会WU记录为上传成功，因此监听每次上传后服务端的响应
    uploader.on( 'uploadAccept', function( object, ret ) {
    	if(ret.code === 0) {
    		parent.is_upload_success = true;
    		return true;
    	} else {
            parent.layer.msg(ret.message, {
                icon: 2,
                time:1000
            });
			return false;
    	}
    });

    $upload.on('click', function() {
        if ( $(this).hasClass( 'disabled' ) ) {
            return false;
        }
		
		// 添加附加参数
		uploader.option('formData', {
            parentDirPath: parent.global_dir_path,
　　　　	tags: $('#tags_id').val()
　　	})
		
        if ( state === 'ready' ) {
            uploader.upload();
        } else if ( state === 'paused' ) {
            uploader.upload();
        } else if ( state === 'uploading' ) {
            uploader.stop();
        }
    });

    $info.on( 'click', '.retry', function() {
        uploader.retry();
    } );
    

    $upload.addClass( 'state-' + state );
    updateTotalProgress();
});
