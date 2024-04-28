import request from '@/utils/request'
export default {
    getList:function(params) {
        return request({
            url: '/dzuser/wallet/address/list',
            method: 'get',
            params
        })
    },
    add:function(params) {
        return request({
            url: '/dzuser/wallet/address',
            method: 'post',
            data: params
        })
    },
    update:function(params) {
        return request({
            url: '/dzuser/wallet/address',
            method: 'PUT',
            data: params
        })
    },
    remove:function(id) {
        return request({
            url: '/dzuser/wallet/address',
            method: 'delete',
            params: {
                id: id
            }
        })
    }
}
