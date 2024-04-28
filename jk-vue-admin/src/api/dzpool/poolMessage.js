import request from '@/utils/request'
export default {
    getList:function(params) {
        return request({
            url: '/dzpool/message/list',
            method: 'get',
            params
        })
    },
    add:function(params) {
        return request({
            url: '/dzpool/message',
            method: 'post',
            data: params
        })
    },
    update:function(params) {
        return request({
            url: '/dzpool/message',
            method: 'PUT',
            data: params
        })
    },
    remove:function(id) {
        return request({
            url: '/dzpool/message',
            method: 'delete',
            params: {
                id: id
            }
        })
    }
}
