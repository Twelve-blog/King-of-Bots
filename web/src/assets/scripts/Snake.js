import { AcGameObject } from "./AcGameObject";
import { Cell } from "./Cell";

export class Snake extends AcGameObject {
    constructor(info, gamemap) {
        super();

        this.id = info.id;
        this.color = info.color;
        this.gamemap = gamemap;

        this.cells = [new Cell(info.r, info.c)]; // 存放蛇的身体， cells[0]存放蛇头
        this.next_cell = null; // 下一步的目标位置

        this.speed = 5; // 蛇每秒走5个格子
        this.direction = -1 ; // 0,1,2,3 上右下左
        this.status = "idle"; // idle表示静止，move 表示正在移动，die表示死亡
        
        this.dr = [-1, 0, 1, 0]; // 行
        this.dc = [0, 1, 0, -1];  // 列

        this.step = 0;// 回合数
        this.eps = 1e-2; // 误差

        this.eye_direction = 0;
        if(this.id === 1) this.eye_direction = 2; // 左下角的蛇初始朝上 

        this.eye_dx = [     // 蛇眼睛不同方向x偏移量
            [-1, 1],
            [1, 1],
            [-1, 1],
            [-1, -1]
        ];
        this.eye_dy = [     // 蛇眼睛不同方向y偏移量
            [-1, -1],
            [-1, 1],
            [1, 1],
            [-1, 1]
        ];
    }

    start() {

    }

    set_direction(d) {
        this.direction = d;
    }

    check_tail_increasing() {   // 检测是否蛇尾变长
        if (this.step <= 10) return true;
        if (this.step % 3 === 1) return true;
        return false;
    }

    next_step() { // 将蛇的状态变成走下一步
        const d = this.direction;
        this.next_cell = new Cell(this.cells[0].r + this.dr[d], this.cells[0].c + this.dc[d]);
        this.eye_direction = d;
        this.direction = -1;
        this.status = "move";
        this.step++;

        const k = this.cells.length;
        for (let i = k; i > 0; i --) {
            this.cells[i] = JSON.parse(JSON.stringify(this.cells[i-1]));
        }

        if(!this.gamemap.check_vaild(this.next_cell)) { // 蛇死亡
            this.status = "die";
        }
    }

    update_move() {
        // this.cells[0].y += this.speed * this.timedelta/1000;
        const dx = this.next_cell.x - this.cells[0].x;
        const dy = this.next_cell.y - this.cells[0].y;
        const distance = Math.sqrt(dx * dx + dy * dy);

        if(distance < this.eps) {
            this.cells[0] = this.next_cell;
            this.next_cell = null;
            this.status = "idle"; // 停下来

            if(!this.check_tail_increasing()){  // 蛇不变长
                this.cells.pop();
            }
        } else {
            const move_distance = this.speed * this.timedelta / 1000;
            this.cells[0].x += move_distance * dx / distance;
            this.cells[0].y += move_distance * dy / distance;

            if(!this.check_tail_increasing()) {
                const k = this.cells.length;
                const tail = this.cells[k-1], tail_target = this.cells[k-2];
                const tail_dx = tail_target.x - tail.x;
                const tail_dy = tail_target.y - tail.y;
                tail.x += move_distance * tail_dx / distance;
                tail.y += move_distance * tail_dy / distance;
            }
        }
    }

    update() {  // 每一帧执行一次
        if(this.status === 'move'){
            this.update_move();
        }
        this.render();
    }

    render() {
        const L = this.gamemap.L;
        const ctx = this.gamemap.ctx;

        ctx.fillStyle = this.color;

        if(this.status == "die") {
            ctx.fillStyle = "white";
        }

        for (const cell of this.cells) {
            ctx.beginPath();
            ctx.arc(cell.x * L, cell.y * L, L/2 * 0.8, 0, Math.PI * 2);
            ctx.fill();
        }

        for (let i = 1; i < this.cells.length; i ++ ) {
            const a = this.cells[i], b = this.cells[i-1];
            if(Math.abs(a.x - b.x) < this.eps && Math.abs(a.y - b.y) < this.eps) {
                continue;
            }
            if(Math.abs(a.x - b.x) < this.eps) {
                ctx.fillRect((a.x - 0.5 + 0.1)  * L, Math.min(a.y, b.y) * L, L * 0.8, Math.abs(a.y-b.y) * L);
            } else {
                ctx.fillRect(Math.min(a.x, b.x) * L, (a.y - 0.5 + 0.1) * L, Math.abs(a.x-b.x) * L, L* 0.8);
            }
        }
        ctx.fillStyle = "black";
        for (let i = 0; i < 2; i++) {
            const eye_x = (this.cells[0].x + this.eye_dx[this.eye_direction][i] * 0.15) * L;
            const eye_y = (this.cells[0].y + this.eye_dy[this.eye_direction][i] * 0.15) * L;
            ctx.beginPath();
            ctx.arc(eye_x, eye_y, L * 0.05, 0, Math.PI * 2);
            ctx.fill();
        }
    }
}