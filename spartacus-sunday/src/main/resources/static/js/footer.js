var tipIndex = null;
function privaycTip() {
    tipIndex = layer.tips('本网站除了提供基本的文章浏览功能以外，还提供了社交登陆、在线聊天等特色功能，因此有必要在此申明本网站的隐私政策。<br><br>' +
        '1、当用户社交登陆（QQ、微信）后，系统会获取用户的昵称、头像等基本信息，同时也会保存这些基本信息；当用户对文章进行点赞、评论，或进行在线聊天时会使用到这些基本信息，但不会用于其他用途，也不会进行非法传播。<br><br>' +
        '2、当用户社交登陆后进行在线聊天（群聊、私聊）时，系统会记录每个用户的聊天信息；这些信息仅用于当用户再次登陆聊天系统后方便用户追溯历史聊天记录，但不会用于其他用途，也不会进行非法传播。',
        '#privacyPolicy',{
        tips: 1,
        time: 600000,
    });
}
function closePrivaycTip() {
    if(tipIndex != null) layer.close(tipIndex);
}