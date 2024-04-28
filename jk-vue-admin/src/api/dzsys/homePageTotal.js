import request from '@/utils/request'
export default {
    getList:function(params) {
        return request({
            url: '/dzsys/homepagetotal/list',
            method: 'get',
            params
        })
    },
    add:function(params) {
        return request({
            url: '/dzsys/homepagetotal',
            method: 'post',
            data: params
        })
    },
    update:function(params) {
        return request({
            url: '/dzsys/homepagetotal',
            method: 'PUT',
            data: params
        })
    },
    remove:function(id) {
        return request({
            url: '/dzsys/homepagetotal',
            method: 'delete',
            params: {
                id: id
            }
        })
    }
}
