
/*
 * @package summernoteDrafts.js
 * @version 1.0
 * @author Jessica González <suki@missallsunday.com>
 * @copyright Copyright (c) 2017, Jessica González
 * @license https://opensource.org/licenses/MIT MIT
 */

(function() {
  (function(factory) {
    if (typeof define === 'function' && define.amd) {
      define(['jquery'], factory);
    } else if (typeof module === 'object' && module.exports) {
      module.exports = factory(require('jquery'));
    } else {
      factory(window.jQuery);
    }
  })(function($) {
    $.extend($.summernote.options, {
      sDrafts: {
        storePrefix: 'sDrafts',
        dateFormat: null,
        saveIcon: null,
        loadIcon: null
      }
    });
    $.extend($.summernote.lang['en-US'], {
      sDrafts: {
        save: 'Save to local',
        load: 'Load from local',
        select: 'Select a draft you want to load !',
        provideName: 'Provide a name for this draft !',
        saved: 'Draft was successfully saved !',
        loaded: 'Draft was successfully loaded !',
        deleteAll: 'Delete all',
        noDraft: 'The selected draft couldn\'t be loaded, try it again or select another one !',
        nosavedDrafts: 'There aren\'t any drafts !',
        deleteDraft: 'Delete',
        deleted: 'Draft was successfully deleted !',
        deletedAll: 'Drafts was successfully deleted !',
        youSure: 'Confirm to delete ?'
      }
    });
    $.extend($.summernote.plugins, {
      'sDraftsSave': function(context) {
        var $editor, lang, options, ui;
        ui = $.summernote.ui;
        options = context.options;
        lang = options.langInfo.sDrafts;
        $editor = context.layoutInfo.editor;
        context.memo('button.sDraftsSave', function() {
          var button;
          button = ui.button({
            contents: '<i class="fa fa-upload"/>',
            tooltip: lang.save,
            click: function(e) {
              e.preventDefault();
              context.invoke('sDraftsSave.show');
              return false;
            }
          });
          return button.render();
        });
        this.initialize = (function(_this) {
          return function() {
            var title = $("#title_id").val();
            var $container, body, footer;
            $container = options.dialogsInBody ? $(document.body) : $editor;
            body = "<div class='form-group'><label>" + lang.provideName + "</label><input class='note-draftName form-control' type='text' /></div>";
            footer = "<button href='#' class='btn btn-primary note-link-btn'>" + lang.save + "</button>";
            _this.$dialog = ui.dialog({
              className: 'link-dialog',
              title: lang.save,
              fade: options.dialogsFade,
              body: body,
              footer: footer
            }).render().appendTo($container);
          };
        })(this);
        this.destroy = (function(_this) {
          return function() {
            ui.hideDialog(_this.$dialog);
            _this.$dialog.remove();
          };
        })(this);
        this.show = (function(_this) {
          return function() {
            var $saveBtn;
            ui.showDialog(_this.$dialog);
            return $saveBtn = _this.$dialog.find('.note-link-btn').click(function(e) {
              var draftName;
              e.preventDefault;
              draftName = _this.$dialog.find('.note-draftName').val();
              _this.saveDraft(draftName);
              return false;
            });
          };
        })(this);
        this.saveDraft = (function(_this) {
          return function(name) {
            var body, date, isoDate, keyName;

            date = new Date();
            isoDate = date.toISOString();

            var re = /(^\s*)|(\s*$)|( )/g; //空格正则表达式
            if ((name == null) || (name.replace(re,'')=='')) {
              // name = isoDate;
              name = convertDateToGivenFormat(date, "yyyyMMddHHmmss");
            }
            keyName = options.sDrafts.storePrefix + '-' + name;
            body = context.code();
            store.set(keyName, {
              name: name,
              sDate: isoDate,
              body: body
            });
            // alert(lang.saved);
            parent.layer.msg(lang.saved, {
              icon: 1,
              time:1000
            });
            _this.destroy();
          };
        })(this);
      }
    });
    return $.extend($.summernote.plugins, {
      'sDraftsLoad': function(context) {
        var $editor, draft, drafts, fn, htmlList, key, lang, options, ui;
        ui = $.summernote.ui;
        options = context.options;
        lang = options.langInfo.sDrafts;
        $editor = context.layoutInfo.editor;
        drafts = [];
        store.each(function(key, value) {
          if (typeof key === 'string' && key.indexOf(options.sDrafts.storePrefix) >= 0) {
            return drafts[key] = value;
          }
        });
        htmlList = '';
        fn = function() {
          var fDate;
          fDate = options.sDrafts.dateFormat && typeof options.sDrafts.dateFormat === 'function' ? options.sDrafts.dateFormat(draft.sDate) : draft.sDate;
          return htmlList += "<li class='list-group-item'><a href='#' class='note-draft' data-draft='" + key + "'>" + draft.name + " - <small>" + fDate + "</small></a><a href='#' class='label label-danger pull-right delete-draft' data-draft='" + key + "'>" + lang.deleteDraft + "</a></li>";
        };
        for (key in drafts) {
          draft = drafts[key];
          fn();
        }
        context.memo('button.sDraftsLoad', function() {
          var button;
          button = ui.button({
            contents: '<i class="fa fa-download"/>',
            tooltip: lang.load,
            click: function(e) {
              e.preventDefault();
              context.invoke('sDraftsLoad.show');
              return false;
            }
          });
          return button.render();
        });
        this.initialize = (function(_this) {
          return function() {
            var $container, body, footer;
            $container = options.dialogsInBody ? $(document.body) : $editor;
            body = htmlList.length ? "<h4>" + lang.select + "</h4><ul class='list-group'>" + htmlList + "</ul>" : "<h4>" + lang.nosavedDrafts + "</h4>";
            footer = htmlList.length ? "<button href='#' class='btn btn-primary deleteAll'>" + lang.deleteAll + "</button>" : "";
            _this.$dialog = ui.dialog({
              className: 'link-dialog',
              title: lang.load,
              fade: options.dialogsFade,
              body: body,
              footer: footer
            }).render().appendTo($container);
          };
        })(this);
        this.destroy = (function(_this) {
          return function() {
            ui.hideDialog(_this.$dialog);
            _this.$dialog.remove();
          };
        })(this);
        this.show = (function(_this) {
          return function() {
            var $deleteAllDrafts, $deleteDraft, $selectedDraft, self;
            ui.showDialog(_this.$dialog);
            self = _this;
            $selectedDraft = _this.$dialog.find('.note-draft').click(function(e) {
              var data, div;
              e.preventDefault;
              div = document.createElement('div');
              key = $(this).data('draft');
              data = drafts[key];
              if (data) {
                div.innerHTML = data.body;
                context.invoke('editor.insertNode', div);
                // alert(lang.loaded);
                parent.layer.msg(lang.loaded, {
                  icon: 1,
                  time:1000
                });
              } else {
                // alert(lang.noDraft);
                parent.layer.msg(lang.noDraft, {
                  icon: 1,
                  time:1000
                });
              }
              self.destroy();
              return false;
            });

            $deleteDraft = _this.$dialog.find('a.delete-draft').click(function(e) {
              var data;

              //询问框
              /*if (confirm(lang.youSure)) {
                key = $(this).data('draft');
                data = drafts[key];
                if (data) {
                  store.remove(key);
                  self = $(this);
                  return self.parent().hide('slow', function() {
                    $(this).remove();
                  });
                } else {
                  return alert(lang.noDraft);
                }
              }*/

              //询问框
              var self = $(this);
              layer.confirm(lang.youSure, {
                btn: ['Yes','No'], //按钮
                yes: function(index) {
                  layer.close(index); //关闭询问框

                  key = self.data('draft');
                  data = drafts[key];
                  if (data) {
                    store.remove(key);
                    parent.layer.msg(lang.deleted, {
                      icon: 1,
                      time:1000
                    });

                    return self.parent().hide('slow', function() {
                      $(this).remove();
                    });
                  } else {
                    return parent.layer.msg(lang.noDraft, {
                      icon: 1,
                      time:1000
                    });
                  }
                },
                cancel: function(index){
                  layer.close(index); //关闭询问框
                }
              });

            });

            $deleteAllDrafts = _this.$dialog.find('button.deleteAll').click(function(e) {
              var fn1, selfButton, uiDialog;
              selfButton = $(this);

              //询问框
              /*if (confirm(lang.youSure)) {
                fn1 = function() {
                  return store.remove(key);
                };
                for (key in drafts) {
                  draft = drafts[key];
                  fn1();
                }
                return uiDialog = self.$dialog.find('ul.list-group').hide('slow', function() {
                  $(this).replaceWith("<h4>" + lang.nosavedDrafts + "</h4>");
                  selfButton.hide('slow');
                });
              }*/

              //询问框
              layer.confirm(lang.youSure, {
                btn: ['Yes','No'], //按钮
                yes: function(index) {
                  layer.close(index); //关闭询问框

                  fn1 = function() {
                    return store.remove(key);
                  };
                  for (key in drafts) {
                    draft = drafts[key];
                    fn1();
                  }

                  parent.layer.msg(lang.deletedAll, {
                    icon: 1,
                    time:1000
                  });

                  return uiDialog = self.$dialog.find('ul.list-group').hide('slow', function() {
                    $(this).replaceWith("<h4>" + lang.nosavedDrafts + "</h4>");
                    selfButton.hide('slow');
                  });
                },
                cancel: function(index){
                  layer.close(index); //关闭询问框
                }
              });

            });
          };
        })(this);
      }
    });
  });

}).call(this);
