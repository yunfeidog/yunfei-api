import {PageContainer} from '@ant-design/pro-components';
import {useModel} from '@umijs/max';
import {Button, Card, Input, message, Modal} from 'antd';
import React, {useEffect, useState} from 'react';
import {changeKeysUsingGet, getKeysUsingGet} from "@/services/yunfeiapi-backend/userController";

const Welcome: React.FC = () => {
  const {initialState} = useModel('@@initialState');

  const [userState, setUserState] = useState<API.User>({});

  useEffect(() => {
    getKeysUsingGet().then((res) => {
      setUserState(res.data as API.User)
    })
  }, [])

  function copyToClipboard(accessKey: string | undefined) {
    if (accessKey) {
      navigator.clipboard.writeText(accessKey).then(() => {
        message.success('复制成功,请妥善保管密钥: ' + accessKey);
      });
    }
  }

  const changeKey = () => {
    Modal.confirm({
      title: '确认更换密钥?',
      content: '更换密钥后,之前的密钥将无法使用,请确认是否继续操作。',
      onOk: () => {
        // 执行更换密钥的逻辑
        changeKeysUsingGet()
        message.success('更换密钥成功');
        //刷新页面
        window.location.reload();
      },
      onCancel: () => {
        message.info('取消更换密钥');
      },
      okText: '确认',
      cancelText: '取消',
    });
  };

  return (
    <PageContainer
      title="用户信息"
      breadcrumb={{
        routes: [
          {
            path: '',
            breadcrumbName: '用户信息',
          },
        ],
      }}

    >
      <Card title="基本信息" style={{marginBottom: 16}}>
        <p>用户ID：{initialState?.currentUser?.id}</p>
        <p>用户名：{initialState?.currentUser?.userName}</p>
        <p>用户角色：{initialState?.currentUser?.userRole}</p>
      </Card>
      <Card title="密钥" style={{marginBottom: 16}} extra={<Button onClick={changeKey} href="#">
        更换密钥
      </Button>}
      >
        <Input.Group compact>
          {/*AccessKey:*/}
          <Input value="AccessKey：" readOnly style={{
            width: '7%', marginRight: 16, border: 'none'
          }}/>
          <Input value={userState.accessKey} readOnly style={{
            width: '20%', marginRight: 16, border: 'none'
          }}/>
          <Button
            onClick={() => copyToClipboard(userState.accessKey)}
            style={{width: '20%'}}
          >
            复制
          </Button>
        </Input.Group>
        <Input.Group compact style={{marginTop: 16}}>
          {/*SecretKey:*/}
          <Input value="SecretKey：" readOnly style={{
            width: '7%', marginRight: 16, border: 'none'
          }}/>
          <Input value={userState.secretKey} readOnly style={{
            width: '20%', marginRight: 16, border: 'none'
          }}/>
          <Button
            onClick={() => copyToClipboard(userState.secretKey)}
            style={{width: '20%'}}
          >
            复制
          </Button>
        </Input.Group>
      </Card>
    </PageContainer>
  );
};

export default Welcome;
