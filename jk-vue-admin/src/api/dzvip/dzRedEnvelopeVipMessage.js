import request from '@/utils/request'
export default {
    getList:function(params) {
        return request({
            url: '/dz/red/envelope/vipmessage/list',
            method: 'get',
            params
        })
    },
    add:function(params) {
        return request({
            url: '/dz/red/envelope/vipmessage',
            method: 'post',
            data: params
        })
    },
    update:function(params) {
        return request({
            url: '/dz/red/envelope/vipmessage',
            method: 'PUT',
            data: params
        })
    },
    remove:function(id) {
        return request({
            url: '/dz/red/envelope/vipmessage',
            method: 'delete',
            params: {
                id: id
            }
        })
    }
}
