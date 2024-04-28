import request from '@/utils/request'
export default {
    getList:function(params) {
        return request({
            url: '/dzuser/pool/list',
            method: 'get',
            params
        })
    },
    add:function(params) {
        return request({
            url: '/dzuser/pool',
            method: 'post',
            data: params
        })
    },
    update:function(params) {
        return request({
            url: '/dzuser/pool',
            method: 'PUT',
            data: params
        })
    },
    remove:function(id) {
        return request({
            url: '/dzuser/pool',
            method: 'delete',
            params: {
                id: id
            }
        })
    }
}
