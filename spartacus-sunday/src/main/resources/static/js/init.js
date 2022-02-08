
//客户端当日身份信息
function getClientCurrentId() {
    var currentDate = new Date().Format("yyyy-MM-dd");
    var clientCurrentId = genUuid();

    var clientIdentity = JSON.parse(localStorage.getItem('spartacus-client-identity') || '{}');
    if(currentDate != clientIdentity.currentDate) {
        var clientIdentity = {"currentDate": currentDate, "clientCurrentId": clientCurrentId};
        localStorage.setItem('spartacus-client-identity', JSON.stringify(clientIdentity))
    } else {
        return clientIdentity.clientCurrentId;
    }
    return clientCurrentId;
}

