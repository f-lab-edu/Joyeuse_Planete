data "aws_vpc" "default" {
  default = true
}

resource "aws_security_group" "allow_tcp_and_ssh" {
  name        = "allow_tcp_and_ssh"
  description = "Allow all TCP traffic and SSH"
  vpc_id      = data.aws_vpc.default.id

  tags = {
    Name = "allow_tcp_and_ssh"
  }
}

resource "aws_vpc_security_group_ingress_rule" "allow_all_tcp" {
  security_group_id = aws_security_group.allow_tcp_and_ssh.id
  cidr_ipv4         = "0.0.0.0/0"
  from_port         = 0
  to_port           = 65535
  ip_protocol       = "tcp"
}


resource "aws_vpc_security_group_ingress_rule" "allow_ssh" {
  security_group_id = aws_security_group.allow_tcp_and_ssh.id
  cidr_ipv4         = "0.0.0.0/0"
  from_port         = 22
  to_port           = 22
  ip_protocol       = "tcp"
}

resource "aws_vpc_security_group_egress_rule" "allow_all_outbound" {
  security_group_id = aws_security_group.allow_tcp_and_ssh.id
  cidr_ipv4         = "0.0.0.0/0"
  ip_protocol       = "-1"
}
